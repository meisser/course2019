<template>
  <div>
    <svg id="stage" class="tradegraph" xmlns="http://www.w3.org/2000/svg"></svg>
    <div id="contextmenu" :class="{context: true, contextmenu: true, in: showContext}" :style="{left: `${contextLeft}px`, top: `${contextTop}px`}">
      <ul class="contextmenu__list">
        <li class="contextmenu__item"><el-button class="contextmenu__btn" id="minichartselection">Add Minichart</el-button></li>
        <li class="contextmenu__item"><el-button class="contextmenu__btn" id="infoselection">Show Info</el-button></li>
        <li class="contextmenu__item"><el-button class="contextmenu__btn" id="childrenselection">Expand/Collapse Children</el-button></li>
      </ul>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3';
import Vue from 'vue';
import { Button } from 'element-ui';

Vue.use(Button);

export default {
  name: 'tradegraph',
  props: ['graphdata', 'selectednode'],
  data() {
    return {
      showContext: false,
      contextLeft: -10000,
      contextTop: 0,
      colors: {
        // these colors should stay in sync with the ones in
        // ./assets/sass/_vars.sass
        consumers: ['#0063a4', '#0b9eff', '#a4dbff'],
        firms: ['#0a7138', '#13ce66', '#86f4b7'],
        links: '#475669',
      },
      graph: {
        // Offset of firms root node from svg origo
        FIRMS_TREE_OFFSET: [600, 400],
        // Offset of consumers root node from svg origo
        CONSUMERS_TREE_OFFSET: [300, 400],
        DEFAULT_NODE_RADIUS: 50,
        // Multiplies with node weight from data
        NODE_RADIUS_FACTOR: 3.5,
        // Offset of links from nodes
        RADIUS_OFFSET: 10,
        INTER_LAYER_GAP: 50,
        INTRA_LAYER_GAP: 10,
        HORIZONTAL_GAP: 50,
        stage: null,
        stageDOM: null,
        // Object that stores coordinates of all nodes
        // used to draw links between nodes
        nodeCoordinates: {},
        // Contains all elements except the panning rectangle,
        // prevents jumping of stage when panning
        global: null,
        // Panning rectangle used for zooming and panning
        panningRect: null,
        defs: null,
        firmNodes: null,
        firmsTree: null,
        firmsTreeDirection: +1,
        consumersNodes: null,
        consumersTree: null,
        consumersTreeDirection: -1,
      },
    };
  },
  mounted() {
    this.graph.stage = d3.select('#stage');
    this.graph.stageDOM = document.getElementById('stage');
    this.graph.rect = this.graph.stage.append('rect');
    this.graph.global = this.graph.stage.append('g');
    this.graph.defs = this.graph.global.append('defs');

    // Create reference marker
    this.graph.defs
      .append('marker')
        .attr('id', 'marker')
        .attr('class', 'marker')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 0)
        .attr('refY', 0)
        .attr('markerWidth', 10)
        .attr('markerHeight', 10)
        .attr('orient', 'auto')
        .attr('markerUnits', 'userSpaceOnUse')
      .append('path')
        .attr('d', 'M0,-5L10,0L0,5');

    this.initPanning();
    this.initClickcage();
    this.updateTradegraph();

    // show mini charts of two root nodes initially
    this.$emit('addminichart', 'consumers', this.colors.consumers[0]);
    this.$emit('addminichart', 'firms', this.colors.firms[0]);
  },
  watch: {
    graphdata() {
      this.updateTradegraph();
    },
    showContext() {
      if (!this.showContext) {
        this.contextLeft = -10000;
      }
    },
  },
  methods: {
    updateTradegraph() {
      this.graph.firmNodes = this.stratifyData(this.graphdata.firms);
      this.graph.consumerNodes = this.stratifyData(this.graphdata.consumers);

      // Clear and update global nodeCoordinates
      this.calculateNodeCoordinates(this.graph.firmNodes);
      this.calculateNodeCoordinates(this.graph.consumerNodes);

      this.drawLinks(this.graphdata.edges);

      this.drawNodes(this.graph.firmNodes);
      this.drawNodes(this.graph.consumerNodes);

      this.initDragging();
    },
    addClickToNodes() {
      d3.selectAll('.node')
        .on('mousedown', () => this.$emit('simstopped'))
        .on('click', (el) => {
          const mouseX = d3.event.pageX;
          const mouseY = d3.event.pageY;

          // Emit click to parent to stop simulation
          this.$emit('nodeclicked', el.data.id);

          if (this.selectednode !== el.data.id) {
            // Show contextmenu
            this.contextLeft = mouseX;
            this.contextTop = mouseY;
            this.showContext = true;

            // Update clickcage property
            this.graph.rect.contextExists = true;
          } else {
            // hide contextmenu
            this.showContext = false;

            // Update clickcage property
            this.graph.rect.contextExists = false;
          }

          d3.selectAll('#minichartselection').on('click',
            () => {
              this.showContext = false;
              this.$emit('nodeclicked');
              this.$emit('addminichart', el.data.id, this.colors[el.data.type][el.depth]);
            },
          );
          d3.selectAll('#infoselection').on('click',
            () => {
              this.showContext = false;
              this.$emit('showinfo', el.data.id, { x: mouseX, y: mouseY });
            },
          );
          d3.selectAll('#childrenselection').on('click',
            () => {
              this.showContext = false;
              this.$emit('showchildren', el.data.id, { x: mouseX, y: mouseY });
            },
          );
        });
    },
    addClickToLinks() {
      d3.selectAll('.links__wrapper').on('click', (d, i) => {
        this.$emit('addminichart', `${this.graphdata.edges[i].source},${this.graphdata.edges[i].destination},${this.graphdata.edges[i].type}`, this.colors.links);
      });
    },
    initDragging() {
      const self = this;
      let thisChildren = null;

      function dragstarted() {
        d3.select(this)
          .classed('dragging', true);

        thisChildren = d3.event.subject.children;
      }

      function dragged() {
        d3.select(this)
          .attr('transform', `translate(${d3.event.x}, ${d3.event.y})`);

        // Watch out for the leading letter n in the id in html
        self.graph.nodeCoordinates[d3.select(this).attr('id').substr(1)].x = d3.event.x;
        self.graph.nodeCoordinates[d3.select(this).attr('id').substr(1)].y = d3.event.y;

        self.drawLinks(self.graphdata.edges);

        // update this node and corresponding edge
        if (d3.event.subject.parent) {
          // TODO: pack into function and use intitally on nodedraw

          d3.select(this).select('.node__edge').attr('d', () => {
            const localX = d3.event.x;
            const localY = d3.event.y;
            const x = self.graph.nodeCoordinates[d3.event.subject.parent.data.id].x - localX;
            const y = self.graph.nodeCoordinates[d3.event.subject.parent.data.id].y - localY;
            const r = d3.event.subject.parent.data.data.size * self.graph.NODE_RADIUS_FACTOR;
            const deltaX = r * x / Math.sqrt((y ** 2) + (x ** 2));
            const deltaY = r * y / Math.sqrt((y ** 2) + (x ** 2));

            return `M 0 0 L${x - deltaX} ${y - deltaY}`;
          });
        }

        // udpate children and corresponding edges
        if (thisChildren) {
          thisChildren.forEach((el) => {
            const x = d3.event.x - self.graph.nodeCoordinates[el.data.id].x;
            const y = d3.event.y - self.graph.nodeCoordinates[el.data.id].y;
            const r = d3.event.subject.data.data.size * self.graph.NODE_RADIUS_FACTOR;
            const deltaX = r * x / Math.sqrt((y ** 2) + (x ** 2));
            const deltaY = r * y / Math.sqrt((y ** 2) + (x ** 2));
            d3.select(`#n${el.data.id} .node__edge`).attr('d', () => `M 0 0 L${x - deltaX} ${y - deltaY}`);
          });
        }
      }

      function dragended() {
        d3.select(this)
          .classed('dragging', false);

        self.graph.nodeCoordinates[d3.event.subject.data.id].dragged = true;
      }

      const drag = d3.drag()
        .on('start', dragstarted)
        .on('drag', dragged)
        .on('end', dragended);

      this.graph.global.selectAll('.node')
        .call(drag);
    },
    initPanning() {
      this.graph.rect
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('fill', 'transparent')
        .call(d3.zoom()
          .scaleExtent([0.5, 4])
          .on('zoom', () => {
            this.graph.global.attr('transform', d3.event.transform);
          }),
        );
    },
    initClickcage() {
      // Use the panning rectangle as a clickcage
      this.graph.rect
        .attr('class', 'clickcage');

      this.graph.rect.contextExists = false;

      this.graph.rect.on('click', () => {
        if (this.graph.rect.contextExists) {
          // Hide contextMenu
          this.showContext = false;
          this.$emit('hidecontextmenus');

          // Update clickcage property
          this.graph.rect.contextExists = false;

          // Emit empty nodeclided to unselect node
          this.$emit('nodeclicked');
        } else {
          // Emit empty nodeclicked to unselect node
          this.$emit('nodeclicked');
        }
      });
    },
    stratifyData(nodeData) {
      // Prepare data for hierarchical layout
      const treeData = d3.stratify()
        .id(d => d.label)
        .parentId(d => d.parent)(nodeData);

      // Get nodes in hierarchical structure
      return d3.hierarchy(treeData, d => d.children);
    },
    calculateNodeCoordinates(nodeData) {
      let previousDepth = 0;
      let layerIterator = 0;
      let rootOffset;
      let horizontalDistance;
      let accumulatedLayerGap = -this.graph.INTRA_LAYER_GAP;

      // Check what tree we are updating and
      // set corresponding offset and horizontal distance
      if (nodeData.data.id === 'firms') {
        rootOffset = this.graph.FIRMS_TREE_OFFSET;
        horizontalDistance = this.graph.firmsTreeDirection * this.graph.HORIZONTAL_GAP;
      } else if (nodeData.data.id === 'consumers') {
        rootOffset = this.graph.CONSUMERS_TREE_OFFSET;
        horizontalDistance = this.graph.consumersTreeDirection * this.graph.HORIZONTAL_GAP;
      }

      nodeData.descendants()
        .forEach((d, i) => {
          let x;

          // Set x coordinate
          if (d.depth === previousDepth && i !== 0) {
            layerIterator += 1;
            x = rootOffset[0] + (layerIterator * horizontalDistance);
          } else {
            x = rootOffset[0];
            layerIterator = 0;
            accumulatedLayerGap += 15;
          }
          // Set y coordinate
          const y = (this.graph.INTER_LAYER_GAP * i) + accumulatedLayerGap + rootOffset[1];

          // Save type for coloring minicharts based on it
          d.data.type = nodeData.data.id;

          // Update previousDepth
          previousDepth = d.depth;

          if (!(d.data.id in this.graph.nodeCoordinates)
              || !this.graph.nodeCoordinates[d.data.id].dragged) {
            // Update nodeCoordinates for later use in drawLinks function
            this.graph
              .nodeCoordinates[d.data.id] = { x, y, size: d.data.data.size, dragged: false };
          }
        });
    },
    drawNodes(nodeData) {
      const self = this;
      const type = (nodeData.data.id === 'firms' ? 'firms' : 'consumers');

      // Create joins
      const nodesJoin = this.graph.global.selectAll(`.node--${nodeData.data.id}`)
        .data(nodeData.descendants());

      // Exit join
      nodesJoin.exit().remove();

      // Add group elements in enter join
      const nodesEnterJoin = nodesJoin.enter();

      const group = nodesEnterJoin
        .append('g')
        .attr('class', d => `node node--${nodeData.data.id} ${(d.children ? 'branch' : 'leaf')}`);

      group
        .append('path')
        .attr('class', 'node__edge');

      // Append node to node group
      group
        .append('circle')
        .attr('class', (d, i) => {
          if (i === 0) {
            return 'node__circle c0';
          }
          return `node__circle c${d.depth}`;
        })
        .attr('cx', 0)
        .attr('cy', 0);

      // Append labels to node group
      group
        .append('text')
        .attr('class', 'node__text')
        .attr('text-anchor', () => {
          if (type === 'firms') {
            return 'start';
          }
          return 'end';
        });

      // Append circle to animate on click
      group
        .append('circle')
        .attr('class', 'node__circle--active')
        .attr('r', 0)
        .attr('cx', 0)
        .attr('cy', 0);

      // Merge enter join with update &
      // transform nodes to calculated position
      const groupJoin = nodesJoin
        .merge(group)
        .raise()
        .attr('id', d => `n${d.data.id}`)
        .classed('active', d => d.data.id === this.selectednode)
        .attr('transform', d => `translate(${self.graph.nodeCoordinates[d.data.id].x},
          ${self.graph.nodeCoordinates[d.data.id].y})`)
        .each((d) => {
          // Update size in nodeCoordinates
          self.graph.nodeCoordinates[d.data.id].size = d.data.data.size;
        });

      groupJoin
        .select('.node__circle')
        .transition()
        .attr('r', d => self.graph.NODE_RADIUS_FACTOR * d.data.data.size);

      groupJoin
        .select('.node__text')
        .transition()
        .attr('dx', (d) => {
          let offsetCoefficient = -0.5;
          let offsetConstant = -0.5;
          if (type === 'firms') {
            offsetCoefficient = 0.5;
            offsetConstant = 0.5;
          }
          return ((self.graph.NODE_RADIUS_FACTOR * d.data.data.size) * offsetCoefficient)
            + offsetConstant;
        })
        .attr('dy', d => -5 + (-1 * (self.graph.NODE_RADIUS_FACTOR * d.data.data.size)))
        .text(d => d.data.id);

      groupJoin
        .select('.node__edge')
        .attr('d', (d, i) => {
          if (i > 0) {
            const localX = this.graph.nodeCoordinates[d.data.id].x;
            const localY = this.graph.nodeCoordinates[d.data.id].y;
            const x = this.graph.nodeCoordinates[d.parent.data.id].x - localX;
            const y = this.graph.nodeCoordinates[d.parent.data.id].y - localY;
            const r = d.parent.data.data.size * self.graph.NODE_RADIUS_FACTOR;
            const deltaX = r * x / Math.sqrt((y ** 2) + (x ** 2));
            const deltaY = r * y / Math.sqrt((y ** 2) + (x ** 2));

            return `M 0 0 L${x - deltaX} ${y - deltaY}`;
          }
          return '';
        });

      // Add click events to nodes
      this.addClickToNodes();
    },
    drawLinks(links) {
      // Remove all (groups) and (links in defs)
      d3.selectAll('.links__wrapper').remove();
      d3.select('#defs-links').remove();

      if (links.length > 0) {
        let currentSource = links[0].source;
        let currentDestination = links[0].destination;

        let globalSourceX = 0;
        let globalSourceY = 0;
        let globalDestinationX = 0;
        let globalDestinationY = 0;
        let deltaX = 0;
        let deltaY = 0;
        let alpha = 0;
        const xSource = 0;
        const ySource = 0;
        let localEdgeCount = 0;

        // Create group in defs to define links
        const defsGroup = this.graph.defs.append('g').attr('id', 'defs-links');

        // Create initial group to append links to
        let group = this.graph.global.append('g')
          .attr('class', 'links__wrapper');

        // Create the enter join
        const linksJoin = group.selectAll('.link')
          .data(links);

        // Exit join
        linksJoin.exit().remove();

        const linksEnterJoin = linksJoin
          .enter();

        linksJoin
          .merge(linksEnterJoin)
          .each((d, i) => {
            if (d.source === currentSource && d.destination === currentDestination && i !== 0) {
              localEdgeCount += 1;
            } else {
              localEdgeCount = 0;
              // create new svg group
              // note: first group has already been created
              if (i !== 0) {
                group = this.graph.global.append('g').attr('class', 'links__wrapper');
              }

              globalSourceX = this.graph.nodeCoordinates[d.source].x;
              globalSourceY = this.graph.nodeCoordinates[d.source].y;

              globalDestinationX = this.graph.nodeCoordinates[d.destination].x;
              globalDestinationY = this.graph.nodeCoordinates[d.destination].y;

              deltaX = globalDestinationX - globalSourceX;
              deltaY = globalDestinationY - globalSourceY;
              let rotationCorrection = 0;

              alpha = Math.atan(deltaY / deltaX) * (180 / Math.PI);

              if (deltaX < 0) {
                rotationCorrection = -180;
              }
              alpha += rotationCorrection;

              group
                .attr('transform', `translate(${globalSourceX}, ${globalSourceY}) rotate(${alpha})`);

              currentSource = d.source;
              currentDestination = d.destination;
            }

            const radiusSource = this.graph.NODE_RADIUS_FACTOR
              * this.graph.nodeCoordinates[currentSource].size
              || this.graph.NODE_RADIUS;
            const radiusDestination = this.graph.NODE_RADIUS_FACTOR
              * this.graph.nodeCoordinates[currentDestination].size
              || this.graph.DEFAULT_NODE_RADIUS;
            const j = localEdgeCount + 2;
            const deltaXLocal = deltaX / Math.cos(alpha * Math.PI / 180);
            // 0.3 rad ^= 17.2 deg
            const x0 = xSource
              + ((radiusSource + this.graph.RADIUS_OFFSET)
              * Math.cos((localEdgeCount + 1) * 0.3));
            const y0 = ySource
              - ((radiusSource + this.graph.RADIUS_OFFSET)
              * Math.sin((localEdgeCount + 1) * 0.3));
            let x1 = deltaXLocal;
            x1 -= ((radiusDestination + this.graph.RADIUS_OFFSET + 8)
              * Math.cos((localEdgeCount + 1) * 0.3));
            const y1 = -(radiusDestination + this.graph.RADIUS_OFFSET + 8)
              * Math.sin((localEdgeCount + 1) * 0.3);

            const cx0 = j * x0;
            const cx1 = (j * (x1 - deltaXLocal)) + deltaXLocal;
            const cy0 = j * y0;
            const cy1 = j * y1;

            // Append the bezier curve and marker
            defsGroup
              .append('path')
              .attr('id', `${currentSource}-${currentDestination}-${i}`)
              .attr('d', `M ${x0} ${y0} C ${cx0} ${cy0}, ${cx1} ${cy1}, ${x1} ${y1}`);

            // Only sane version is to add an inverse path for
            // label positioning
            if (deltaX < 0) {
              defsGroup
                .append('path')
                .attr('id', `${currentSource}-${currentDestination}-${i}-inverse`)
                .attr('d', `M ${x1} ${y1} C ${cx1} ${cy1}, ${cx0} ${cy0}, ${x0} ${y0}`);
            }

            group
              .append('use')
              .attr('xlink:href', `#${currentSource}-${currentDestination}-${i}`)
              .attr('class', 'link')
              .attr('stroke-width', `${d.weight * 1.5}px`)
              .attr('marker-end', () => 'url(#marker)');

            // additional group to set transform-origin for text rotation
            // when deltaX < 0 (links move from right to left)
            const textGroup = group
              .append('g')
              .attr('class', 'link__label');

            const text = textGroup
              .append('text')
              .attr('text-anchor', 'middle');

            text
              .append('textPath')
              // .attr('xlink:href', `#${currentSource}-${currentDestination}-${i}`)
              .attr('xlink:href', () => {
                if (deltaX < 0) {
                  return `#${currentSource}-${currentDestination}-${i}-inverse`;
                }
                return `#${currentSource}-${currentDestination}-${i}`;
              })
              .text(d.label)
              .attr('startOffset', '50%');
          });
      }

      this.addClickToLinks();
    },
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

$tradegraph-coral:                                    $coral
$grey:                                               #676767
$light-grey:                                  rgba(0,0,0,.3)

$tradegraph-black:                                    $black
$tradegraph-light-black:                        $light-black
$tradegraph-extra-light-black:            $extra-light-black

$tradegraph-blue:                                      $blue
$tradegraph-light-blue:                          $light-blue
$tradegraph-extra-light-blue:              $extra-light-blue
$tradegraph-dark-blue:                            $dark-blue
$tradegraph-extra-dark-blue:                $extra-dark-blue

$tradegraph-green:                                    $green
$tradegraph-light-green:                        $light-green
$tradegraph-extra-light-green:            $extra-light-green
$tradegraph-dark-green:                          $dark-green
$tradegraph-extra-dark-green:              $extra-dark-green

.tradegraph
  position: absolute
  left: 0
  right: 0
  top: 0
  bottom: 0
  width: 100%
  height: 100%

.node

  &.dragging
    .node__circle
      @for $i from 0 through 4
        &.c#{$i}
          opacity: 1
          fill: $tradegraph-coral

  &.active
    .node__circle
      stroke-width: 4px
      r: 30px
      @for $i from 0 through 4
        &.c#{$i}
          fill: transparent

      &--active
        r: 15px

  &__edge
    stroke-width: 1px
    stroke: $tradegraph-extra-light-black
    opacity: .2

  &__text
    font: bold 12px/1 Helvetica, Arial, sans-serif
    text-transform: uppercase
    fill: $tradegraph-light-black
    transition: all .2s
    cursor: pointer

  &__action
    font: normal 12px/1 Helvetica, Arial, sans-serif
    text-transform: uppercase
    text-decoration: underline
    fill: $tradegraph-blue

  &__circle
    position: relative
    transition: all .2s ease-out
    cursor: pointer
    stroke-width: 0
    &:hover
      opacity: .8

    &--active
      transition: all .2s
      pointer-events: none

  &--firms
    .node
      &__circle
        fill: $tradegraph-green
        stroke: $tradegraph-coral
        &.c0
          fill: $tradegraph-dark-green
        &.c1
          fill: $tradegraph-green
        &.c2
          fill: $tradegraph-light-green
        &--active
          fill: $tradegraph-coral

  &--consumers
    .node
      &__circle
        fill: $tradegraph-blue
        stroke: $tradegraph-coral
        &.c0
          fill: $tradegraph-dark-blue
        &.c1
          fill: $tradegraph-blue
        &.c2
          fill: $tradegraph-light-blue
        &--active
          fill: $tradegraph-coral

  &--out
    fill-opacity: .1
    stroke-opacity: .1

  &.branch
    // cursor: pointer
    // .node
    //   &__circle
    //     stroke-width: 2px
    // &.node
    //   &--firms
    //     .node
    //       &__circle
    //         stroke: darken($coral, 10%)
    //   &--consumers
    //     .node
    //       &__circle
    //         stroke: darken($blue, 15%)

.links
  &__wrapper
    cursor: pointer
    &:hover
      text
        fill: $tradegraph-black
      .link
        stroke: $tradegraph-green

.link
  fill: none
  stroke: $light-grey
  // animation: pulsate 3s
  animation-iteration-count: infinite
  &:nth-child(1)
    animation-delay: 0.2s
  &:nth-child(2)
    animation-delay: 0.5s
  &:nth-child(3)
    animation-delay: 0.6s
  &:nth-child(4)
    animation-delay: 1s
  &:nth-child(5)
    animation-delay: 0.4s
  &:nth-child(6)
    animation-delay: 2.2s
  &:nth-child(7)
    animation-delay: 1.2s
  &:nth-child(8)
    animation-delay: 0.1s
  &:nth-child(9)
    animation-delay: 3s
  &:nth-child(10)
    animation-delay: 2.6s
  &:nth-child(11)
    animation-delay: 2s
  &:nth-child(12)
    animation-delay: 1.6s
  &:nth-child(13)
    animation-delay: 0.4s
  &:nth-child(14)
    animation-delay: 0.5s
  &:nth-child(15)
    animation-delay: 0.1s
  &:nth-child(16)
    animation-delay: 1.9s

  &__label
    transform-origin: center center
    transition: transform 1s 1s
    font-size: 12px
    fill: $tradegraph-light-black
    &.rot
      transform: rotate(180deg)

.marker
  fill: $light-grey

.contextmenu

  &__list
    padding: 0
    margin: 0
    list-style-type: none

  &__item
    display: block
    + .contextmenu__item
      margin-top: 5px

  &__btn
    display: block
    width: 100%

@keyframes pulsate
  0%
    opacity: 1
  30%
    opacity: .3
  100%
    opacity: 1
</style>
