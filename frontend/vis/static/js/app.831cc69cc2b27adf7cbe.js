webpackJsonp([1],{11:function(t,e,n){"use strict";e.a={apiURL:"http://"+window.location.hostname+":8080",stepSizeOptions:[1,2,5,10,100],miniCharts:{noOfChartsInSidebar:5,height:300,internalHeight:150},xhrConfig:{mode:"cors"},handleFetchErrors:function(t){if(!t.ok)throw Error(t.statusText);return t},alertError:function(t){alert(t)}}},219:function(t,e){},220:function(t,e){},221:function(t,e){},223:function(t,e,n){"use strict";var i=n(2),a=n(612),r=n(595),s=n.n(r),o=n(598),c=n.n(o),d=n(600),l=n.n(d);i.default.use(a.a),e.a=new a.a({mode:"history",routes:[{path:"/vis",name:"home",component:s.a},{path:"/vis/simulation",name:"simulation",component:c.a},{path:"/vis/trades",name:"trades",component:l.a}]})},224:function(t,e,n){function i(t){n(578)}var a=n(8)(n(226),n(602),i,null,null);t.exports=a.exports},225:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(2),a=n(224),r=n.n(a),s=n(223);new i.default({el:"#app",router:s.a,render:function(t){return t(r.a)}})},226:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),e.default={name:"app"}},227:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(219),a=(n.n(i),n(210)),r=n.n(a),s=n(220),o=(n.n(s),n(211)),c=n.n(o),d=n(221),l=(n.n(d),n(212)),h=n.n(l),u=n(43),f=(n.n(u),n(42)),p=(n.n(f),n(25)),m=n.n(p),v=n(2),_=n(222),g=n.n(_),w=n(11);v.default.use(m.a),v.default.use(h.a),v.default.use(c.a),v.default.use(r.a),e.default={name:"chart",props:["simulationid","suggestedmetric"],watch:{suggestedmetric:function(){this.selectedSeries=["Loading..."],this.updateChart()},selectedSeries:function(){this.renderRemove=this.selectedSeries.length>1,this.renderAdd=this.selectedSeries.length<this.series.length}},data:function(){return{metrics:[],description:"",selectedSeries:["Loading..."],series:[],renderAdd:!1,renderRemove:!1,renderRefresh:!1}},created:function(){var t=this;fetch(w.a.apiURL+"/metrics",w.a.xhrConfig).then(w.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){e.metrics.forEach(function(e){t.metrics.push(e)})}).catch(function(t){return w.a.alertError(t)}),this.updateChart()},mounted:function(){this.initChart()},methods:{updateMetric:function(t){this.$emit("updateUrl",t)},updateSeries:function(t){var e=t[0];this.selectedSeries[e]=t[1],this.updateChart()},addSeries:function(){var t=this,e=this.selectedSeries.length,n=this.series.indexOf(this.selectedSeries[e-1])+1,i=this.series[n];n<this.series.length&&!this.selectedSeries.includes(i)?this.selectedSeries.push(i):this.series.forEach(function(n){e===t.selectedSeries.length&&(t.selectedSeries.includes(n)||t.selectedSeries.push(n))}),this.updateChart()},removeSeries:function(){this.selectedSeries.pop(),this.updateChart()},download:function(){window.open(w.a.apiURL+"/downloadcsv?metric="+this.suggestedmetric+"&sim="+this.simulationid,"_blank")},initChart:function(){var t={autosize:!0,width:1e3,height:500,margin:{l:50,r:50,t:10,b:10}},e=[{x:[0,100],y:[1,1],mode:"lines",type:"scatter"}];g.a.newPlot("chart",e,t,{displayModeBar:!1})},updateChart:function(){var t=this;fetch(w.a.apiURL+"/chart?sim="+this.simulationid+"&metric="+this.suggestedmetric+"&row="+this.selectedSeries.join(","),w.a.xhrConfig).then(w.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.description=e.description,t.series=e.options;var n=[],i=[];e.data.forEach(function(t){i.push({x:t.xs,y:t.ys,name:t.name,mode:"lines",type:"scatter"}),n.push(t.name)});var a={autosize:!0,margin:{l:50,r:50,t:10,b:50}};t.renderRefresh=!e.complete,t.selectedSeries=n,g.a.newPlot("chart",i,a)}).catch(function(t){return w.a.alertError(t)})}}}},228:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(43),a=(n.n(i),n(42)),r=(n.n(a),n(25)),s=n.n(r),o=n(2),c=n(11);o.default.use(s.a),e.default={name:"childrenselection",props:["show","childrenof","activenodes","simulationday","simulationid"],data:function(){return{loading:!0,children:[],selectedChildren:[],allSelected:!1}},computed:{activeNodeArray:function(){return this.activenodes.split(",")}},watch:{show:function(){var t=this;this.show&&(this.loading=!0,fetch(c.a.apiURL+"/children?sim="+this.simulationid+"&day="+this.simulationday+"&selection="+this.childrenof,c.a.xhrConfig).then(c.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.children=[],e.children.forEach(function(e){return t.children.push(e.label)}),t.activeChildren=t.children.filter(function(e){return t.activeNodeArray.includes(e)}),t.activeChildren.length===t.children.length?t.allSelected=!0:t.allSelected=!1,t.loading=!1}).catch(function(t){return c.a.alertError(t)}))},allSelected:function(){this.allSelected?this.activeChildren=this.children:this.activeChildren=[]}},methods:{saveSelection:function(){var t=this,e=this.activeNodeArray.filter(function(e){return!t.children.includes(e)});e=e.concat(this.activeChildren),this.$emit("setactivenodes",e.join()),this.$emit("update:show",!1)},cancelSelection:function(){this.$emit("update:show",!1)}}}},229:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(11);e.default={name:"consumerranking",props:["simulationid"],data:function(){return{loading:!0,ranking:null}},created:function(){var t=this;fetch(i.a.apiURL+"/ranking?sim="+this.$route.query.sim,i.a.xhrConfig).then(i.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.ranking=e.list,t.loading=!1}).catch(function(t){return i.a.alertError(t)})}}},230:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(11);e.default={name:"firmranking",props:["simulationid"],data:function(){return{loading:!0,ranking:null}},created:function(){var t=this;fetch(i.a.apiURL+"/firmranking?sim="+this.$route.query.sim,i.a.xhrConfig).then(i.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.ranking=e.list,t.loading=!1}).catch(function(t){return i.a.alertError(t)})}}},231:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(11);e.default={name:"home",data:function(){return{loading:!0,simulations:null}},created:function(){var t=this;fetch(i.a.apiURL+"/list",i.a.xhrConfig).then(i.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.simulations=e.sims,t.loading=!1}).catch(function(t){return i.a.alertError(t)})}}},232:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(238),a=n.n(i),r=n(222),s=n.n(r),o=n(11);e.default={name:"minicharts",props:["agents","simulationday","simulationid"],data:function(){return{chartData:{},graphTitle:{}}},watch:{agents:function(t,e){var n=this,i=t.filter(function(t){return e.indexOf(t)<0});setTimeout(function(){i.forEach(function(t){return n.initChart(t)}),n.fetchCharts()},1)},simulationday:function(){this.fetchCharts()}},methods:{initChart:function(t){var e={autosize:!0,width:300,height:o.a.miniCharts.internalHeight,margin:{l:40,r:30,t:10,b:30}},n=[{x:[],y:[],mode:"lines",line:{color:t.color},type:"scatter"}];s.a.newPlot("minichart-"+t.id,n,e,{displayModeBar:!1})},fetchCharts:function(){var t=this;this.chartData={},this.agents.forEach(function(e){fetch(o.a.apiURL+"/minichart?sim="+t.simulationid+"&day="+t.simulationday+"&selection="+e.id+"&height="+o.a.miniCharts.height,o.a.xhrConfig).then(o.a.handleFetchErrors).then(function(t){return t.json()}).then(function(n){t.$set(t.chartData,e.id,n),t.$set(t.graphTitle,e.id,n.name)}).then(function(){var n=1/o.a.miniCharts.height,i=t.chartData[e.id].max-t.chartData[e.id].min,r={x:[[].concat(a()(Array(t.chartData[e.id].data.length).keys())).map(function(n){return t.simulationday-t.chartData[e.id].data.length+1+n})],y:[t.chartData[e.id].data.map(function(a){return i*a*n+t.chartData[e.id].min})]};s.a.update("minichart-"+e.id,r)}).catch(function(t){return o.a.alertError(t)})})}}}},233:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(43),a=(n.n(i),n(42)),r=(n.n(a),n(25)),s=n.n(r),o=n(2),c=n(11);o.default.use(s.a),e.default={name:"nodeinfo",props:["show","agent","simulationday","simulationid"],data:function(){return{loading:!0,info:{}}},watch:{show:function(){var t=this;this.show&&(this.loading=!0,fetch(c.a.apiURL+"/agents?sim="+this.simulationid+"&day="+this.simulationday+"&selection="+this.agent,c.a.xhrConfig).then(c.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.info=e,t.loading=!1}).catch(function(t){return c.a.alertError(t)}))}},methods:{closeInfo:function(){this.$emit("update:show",!1)}}}},234:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(591),a=n.n(i),r=n(593),s=n.n(r),o=n(594),c=n.n(o),d=n(11);e.default={name:"simulationview",components:{Consumerranking:s.a,Firmranking:c.a,Chart:a.a},data:function(){return{loading:!0,simDescription:"",simInfo:null,simId:this.$route.query.sim,datametric:this.$route.query.metric?this.$route.query.metric:"production"}},methods:{updateUrl:function(t){this.$router.replace({name:"simulation",query:{sim:this.simId,metric:t}}),this.datametric=t}},created:function(){var t=this;fetch(d.a.apiURL+"/info?sim="+this.$route.query.sim,d.a.xhrConfig).then(d.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.simDescription=e.name,t.simInfo=e,t.loading=!1}).catch(function(t){return d.a.alertError(t)})}}},235:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(43),a=(n.n(i),n(42)),r=(n.n(a),n(25)),s=n.n(r),o=n(556);n(2).default.use(s.a),e.default={name:"tradegraph",props:["graphdata","selectednode"],data:function(){return{showContext:!1,contextLeft:-1e4,contextTop:0,colors:{consumers:["#0063a4","#0b9eff","#a4dbff"],firms:["#0a7138","#13ce66","#86f4b7"],links:"#475669"},graph:{FIRMS_TREE_OFFSET:[600,400],CONSUMERS_TREE_OFFSET:[300,400],DEFAULT_NODE_RADIUS:50,NODE_RADIUS_FACTOR:3.5,RADIUS_OFFSET:10,INTER_LAYER_GAP:50,INTRA_LAYER_GAP:10,HORIZONTAL_GAP:50,stage:null,stageDOM:null,nodeCoordinates:{},global:null,panningRect:null,defs:null,firmNodes:null,firmsTree:null,firmsTreeDirection:1,consumersNodes:null,consumersTree:null,consumersTreeDirection:-1}}},mounted:function(){this.graph.stage=o.a("#stage"),this.graph.stageDOM=document.getElementById("stage"),this.graph.rect=this.graph.stage.append("rect"),this.graph.global=this.graph.stage.append("g"),this.graph.defs=this.graph.global.append("defs"),this.graph.defs.append("marker").attr("id","marker").attr("class","marker").attr("viewBox","0 -5 10 10").attr("refX",0).attr("refY",0).attr("markerWidth",10).attr("markerHeight",10).attr("orient","auto").attr("markerUnits","userSpaceOnUse").append("path").attr("d","M0,-5L10,0L0,5"),this.initPanning(),this.initClickcage(),this.updateTradegraph(),this.$emit("addminichart","consumers",this.colors.consumers[0]),this.$emit("addminichart","firms",this.colors.firms[0])},watch:{graphdata:function(){this.updateTradegraph()},showContext:function(){this.showContext||(this.contextLeft=-1e4)}},methods:{updateTradegraph:function(){this.graph.firmNodes=this.stratifyData(this.graphdata.firms),this.graph.consumerNodes=this.stratifyData(this.graphdata.consumers),this.calculateNodeCoordinates(this.graph.firmNodes),this.calculateNodeCoordinates(this.graph.consumerNodes),this.drawLinks(this.graphdata.edges),this.drawNodes(this.graph.firmNodes),this.drawNodes(this.graph.consumerNodes),this.initDragging()},addClickToNodes:function(){var t=this;o.b(".node").on("mousedown",function(){return t.$emit("simstopped")}).on("click",function(e){var n=o.c.pageX,i=o.c.pageY;t.$emit("nodeclicked",e.data.id),t.selectednode!==e.data.id?(t.contextLeft=n,t.contextTop=i,t.showContext=!0,t.graph.rect.contextExists=!0):(t.showContext=!1,t.graph.rect.contextExists=!1),o.b("#minichartselection").on("click",function(){t.showContext=!1,t.$emit("nodeclicked"),t.$emit("addminichart",e.data.id,t.colors[e.data.type][e.depth])}),o.b("#infoselection").on("click",function(){t.showContext=!1,t.$emit("showinfo",e.data.id,{x:n,y:i})}),o.b("#childrenselection").on("click",function(){t.showContext=!1,t.$emit("showchildren",e.data.id,{x:n,y:i})})})},addClickToLinks:function(){var t=this;o.b(".links__wrapper").on("click",function(e,n){t.$emit("addminichart",t.graphdata.edges[n].source+","+t.graphdata.edges[n].destination+","+t.graphdata.edges[n].type,t.colors.links)})},initDragging:function(){function t(){o.a(this).classed("dragging",!0),a=o.c.subject.children}function e(){o.a(this).attr("transform","translate("+o.c.x+", "+o.c.y+")"),i.graph.nodeCoordinates[o.a(this).attr("id").substr(1)].x=o.c.x,i.graph.nodeCoordinates[o.a(this).attr("id").substr(1)].y=o.c.y,i.drawLinks(i.graphdata.edges),o.c.subject.parent&&o.a(this).select(".node__edge").attr("d",function(){var t=o.c.x,e=o.c.y,n=i.graph.nodeCoordinates[o.c.subject.parent.data.id].x-t,a=i.graph.nodeCoordinates[o.c.subject.parent.data.id].y-e,r=o.c.subject.parent.data.data.size*i.graph.NODE_RADIUS_FACTOR;return"M 0 0 L"+(n-r*n/Math.sqrt(Math.pow(a,2)+Math.pow(n,2)))+" "+(a-r*a/Math.sqrt(Math.pow(a,2)+Math.pow(n,2)))}),a&&a.forEach(function(t){var e=o.c.x-i.graph.nodeCoordinates[t.data.id].x,n=o.c.y-i.graph.nodeCoordinates[t.data.id].y,a=o.c.subject.data.data.size*i.graph.NODE_RADIUS_FACTOR,r=a*e/Math.sqrt(Math.pow(n,2)+Math.pow(e,2)),s=a*n/Math.sqrt(Math.pow(n,2)+Math.pow(e,2));o.a("#n"+t.data.id+" .node__edge").attr("d",function(){return"M 0 0 L"+(e-r)+" "+(n-s)})})}function n(){o.a(this).classed("dragging",!1),i.graph.nodeCoordinates[o.c.subject.data.id].dragged=!0}var i=this,a=null,r=o.d().on("start",t).on("drag",e).on("end",n);this.graph.global.selectAll(".node").call(r)},initPanning:function(){var t=this;this.graph.rect.attr("width","100%").attr("height","100%").attr("fill","transparent").call(o.e().scaleExtent([.5,4]).on("zoom",function(){t.graph.global.attr("transform",o.c.transform)}))},initClickcage:function(){var t=this;this.graph.rect.attr("class","clickcage"),this.graph.rect.contextExists=!1,this.graph.rect.on("click",function(){t.graph.rect.contextExists?(t.showContext=!1,t.$emit("hidecontextmenus"),t.graph.rect.contextExists=!1,t.$emit("nodeclicked")):t.$emit("nodeclicked")})},stratifyData:function(t){var e=o.f().id(function(t){return t.label}).parentId(function(t){return t.parent})(t);return o.g(e,function(t){return t.children})},calculateNodeCoordinates:function(t){var e=this,n=0,i=0,a=void 0,r=void 0,s=-this.graph.INTRA_LAYER_GAP;"firms"===t.data.id?(a=this.graph.FIRMS_TREE_OFFSET,r=this.graph.firmsTreeDirection*this.graph.HORIZONTAL_GAP):"consumers"===t.data.id&&(a=this.graph.CONSUMERS_TREE_OFFSET,r=this.graph.consumersTreeDirection*this.graph.HORIZONTAL_GAP),t.descendants().forEach(function(o,c){var d=void 0;o.depth===n&&0!==c?(i+=1,d=a[0]+i*r):(d=a[0],i=0,s+=15);var l=e.graph.INTER_LAYER_GAP*c+s+a[1];o.data.type=t.data.id,n=o.depth,o.data.id in e.graph.nodeCoordinates&&e.graph.nodeCoordinates[o.data.id].dragged||(e.graph.nodeCoordinates[o.data.id]={x:d,y:l,size:o.data.data.size,dragged:!1})})},drawNodes:function(t){var e=this,n=this,i="firms"===t.data.id?"firms":"consumers",a=this.graph.global.selectAll(".node--"+t.data.id).data(t.descendants());a.exit().remove();var r=a.enter(),s=r.append("g").attr("class",function(e){return"node node--"+t.data.id+" "+(e.children?"branch":"leaf")});s.append("path").attr("class","node__edge"),s.append("circle").attr("class",function(t,e){return 0===e?"node__circle c0":"node__circle c"+t.depth}).attr("cx",0).attr("cy",0),s.append("text").attr("class","node__text").attr("text-anchor",function(){return"firms"===i?"start":"end"}),s.append("circle").attr("class","node__circle--active").attr("r",0).attr("cx",0).attr("cy",0);var o=a.merge(s).raise().attr("id",function(t){return"n"+t.data.id}).classed("active",function(t){return t.data.id===e.selectednode}).attr("transform",function(t){return"translate("+n.graph.nodeCoordinates[t.data.id].x+",\n          "+n.graph.nodeCoordinates[t.data.id].y+")"}).each(function(t){n.graph.nodeCoordinates[t.data.id].size=t.data.data.size});o.select(".node__circle").transition().attr("r",function(t){return n.graph.NODE_RADIUS_FACTOR*t.data.data.size}),o.select(".node__text").transition().attr("dx",function(t){var e=-.5,a=-.5;return"firms"===i&&(e=.5,a=.5),n.graph.NODE_RADIUS_FACTOR*t.data.data.size*e+a}).attr("dy",function(t){return n.graph.NODE_RADIUS_FACTOR*t.data.data.size*-1-5}).text(function(t){return t.data.id}),o.select(".node__edge").attr("d",function(t,i){if(i>0){var a=e.graph.nodeCoordinates[t.data.id].x,r=e.graph.nodeCoordinates[t.data.id].y,s=e.graph.nodeCoordinates[t.parent.data.id].x-a,o=e.graph.nodeCoordinates[t.parent.data.id].y-r,c=t.parent.data.data.size*n.graph.NODE_RADIUS_FACTOR;return"M 0 0 L"+(s-c*s/Math.sqrt(Math.pow(o,2)+Math.pow(s,2)))+" "+(o-c*o/Math.sqrt(Math.pow(o,2)+Math.pow(s,2)))}return""}),this.addClickToNodes()},drawLinks:function(t){var e=this;if(o.b(".links__wrapper").remove(),o.a("#defs-links").remove(),t.length>0){var n=t[0].source,i=t[0].destination,a=0,r=0,s=0,c=0,d=0,l=0,h=0,u=0,f=this.graph.defs.append("g").attr("id","defs-links"),p=this.graph.global.append("g").attr("class","links__wrapper"),m=p.selectAll(".link").data(t);m.exit().remove();var v=m.enter();m.merge(v).each(function(t,o){if(t.source===n&&t.destination===i&&0!==o)u+=1;else{u=0,0!==o&&(p=e.graph.global.append("g").attr("class","links__wrapper")),a=e.graph.nodeCoordinates[t.source].x,r=e.graph.nodeCoordinates[t.source].y,s=e.graph.nodeCoordinates[t.destination].x,c=e.graph.nodeCoordinates[t.destination].y,d=s-a,l=c-r;var m=0;h=Math.atan(l/d)*(180/Math.PI),d<0&&(m=-180),h+=m,p.attr("transform","translate("+a+", "+r+") rotate("+h+")"),n=t.source,i=t.destination}var v=e.graph.NODE_RADIUS_FACTOR*e.graph.nodeCoordinates[n].size||e.graph.NODE_RADIUS,_=e.graph.NODE_RADIUS_FACTOR*e.graph.nodeCoordinates[i].size||e.graph.DEFAULT_NODE_RADIUS,g=u+2,w=d/Math.cos(h*Math.PI/180),C=0+(v+e.graph.RADIUS_OFFSET)*Math.cos(.3*(u+1)),y=0-(v+e.graph.RADIUS_OFFSET)*Math.sin(.3*(u+1)),x=w;x-=(_+e.graph.RADIUS_OFFSET+8)*Math.cos(.3*(u+1));var S=-(_+e.graph.RADIUS_OFFSET+8)*Math.sin(.3*(u+1)),k=g*C,E=g*(x-w)+w,R=g*y,b=g*S;f.append("path").attr("id",n+"-"+i+"-"+o).attr("d","M "+C+" "+y+" C "+k+" "+R+", "+E+" "+b+", "+x+" "+S),d<0&&f.append("path").attr("id",n+"-"+i+"-"+o+"-inverse").attr("d","M "+x+" "+S+" C "+E+" "+b+", "+k+" "+R+", "+C+" "+y),p.append("use").attr("xlink:href","#"+n+"-"+i+"-"+o).attr("class","link").attr("stroke-width",1.5*t.weight+"px").attr("marker-end",function(){return"url(#marker)"}),p.append("g").attr("class","link__label").append("text").attr("text-anchor","middle").append("textPath").attr("xlink:href",function(){return d<0?"#"+n+"-"+i+"-"+o+"-inverse":"#"+n+"-"+i+"-"+o}).text(t.label).attr("startOffset","50%")})}this.addClickToLinks()}}}},236:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=n(576),a=(n.n(i),n(566)),r=n.n(a),s=n(575),o=(n.n(s),n(565)),c=n.n(o),d=n(574),l=(n.n(d),n(563)),h=n.n(l),u=n(219),f=(n.n(u),n(210)),p=n.n(f),m=n(220),v=(n.n(m),n(211)),_=n.n(v),g=n(221),w=(n.n(g),n(212)),C=n.n(w),y=n(43),x=(n.n(y),n(42)),S=(n.n(x),n(25)),k=n.n(S),E=n(2),R=n(599),b=n.n(R),D=n(592),A=n.n(D),N=n(597),T=n.n(N),I=n(596),L=n.n(I),M=n(11);E.default.use(k.a),E.default.use(C.a),E.default.use(_.a),E.default.use(p.a),E.default.use(h.a),E.default.use(c.a),E.default.use(r.a),e.default={name:"tradeview",components:{Tradegraph:b.a,Childselection:A.a,Nodeinfo:T.a,Minicharts:L.a},data:function(){return{config:M.a,tradeGraphData:null,loaded:!1,playing:!1,playInterval:null,metrics:[],selectedNode:this.$route.query.selected,miniCharts:[],showNodeInfo:!1,infoOf:null,infoLeft:-1e4,infoTop:0,showChildSelection:!1,childSelectionLeft:-1e4,childSelectionTop:0,childrenOf:null,simId:this.$route.query.sim,simDay:parseInt(this.$route.query.day,10),simAgents:this.$route.query.selection,simStep:parseInt(this.$route.query.step,10),simLength:null}},created:function(){var t=this;this.fetchData(),fetch(M.a.apiURL+"/info?sim="+this.simId,M.a.xhrConfig).then(M.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.simLength=e.days}).catch(function(t){return M.a.alertError(t)}),fetch(M.a.apiURL+"/metrics",M.a.xhrConfig).then(M.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){e.metrics.forEach(function(e){t.metrics.push(e)})}).catch(function(t){return M.a.alertError(t)})},watch:{$route:"fetchData",playing:function(){this.playing?this.playInterval=setInterval(this.nextStep,100):clearInterval(this.playInterval)},simDay:function(){this.goToNewURL()},simAgents:function(){this.goToNewURL()},simStep:function(){this.goToNewURL()},showNodeInfo:function(){this.showNodeInfo||(this.infoLeft=-1e4,this.selectedNode=!1,this.goToNewURL())},showChildSelection:function(){this.showChildSelection||(this.childSelectionLeft=-1e4,this.selectedNode=!1,this.goToNewURL())}},methods:{nextStep:function(){this.simDay=Math.min(this.simDay+this.simStep,this.simLength),this.simDay===this.simLength&&(this.playing=!1)},prevStep:function(){this.simDay=Math.max(this.simDay-this.simStep,0)},goToNewURL:function(){this.hideContextMenus(),this.$router.replace({name:"trades",query:{sim:this.simId,day:this.simDay,selection:this.simAgents,step:this.simStep,selected:this.selectedNode}})},fetchData:function(){var t=this;fetch(M.a.apiURL+"/tradegraph?sim="+this.simId+"&day="+this.simDay+"&selection="+this.simAgents+"&step="+this.simStep,M.a.xhrConfig).then(M.a.handleFetchErrors).then(function(t){return t.json()}).then(function(e){t.tradeGraphData=e,t.loaded=!0,e.hint.length>0&&(t.simAgents+=","+e.hint.join())}).catch(function(t){return M.a.alertError(t)})},handleDownload:function(t){window.open(M.a.apiURL+"/downloadcsv?metric="+t+"&sim="+this.simId+"&day="+this.simDay+"&selection="+this.simAgents+"&step="+this.simStep,"_blank")},handleNodeClicked:function(t){this.playing=!1,this.selectedNode&&this.selectedNode===t?(this.selectedNode=null,this.showchildren=!1,this.showinfo=!1):this.selectedNode=t,this.goToNewURL()},handleAddMinichart:function(t,e){this.miniCharts=this.miniCharts.filter(function(e){return e.id!==t}),this.miniCharts.length>=M.a.miniCharts.noOfChartsInSidebar&&this.miniCharts.pop(),this.miniCharts.unshift({id:t,color:e})},handleShowInfo:function(t,e){this.hideContextMenus(),this.infoOf=t,this.infoLeft=e.x,this.infoTop=e.y,this.showNodeInfo=!0},handleShowChildSelection:function(t,e){this.hideContextMenus(),this.childrenOf=t,this.childSelectionLeft=e.x,this.childSelectionTop=e.y,this.showChildSelection=!0},handleSetActiveNodes:function(t){this.simAgents=t},handleSimStopped:function(){this.playing=!1},hideContextMenus:function(){this.showNodeInfo=!1,this.showChildSelection=!1}}}},42:function(t,e){},43:function(t,e){},574:function(t,e){},575:function(t,e){},576:function(t,e){},577:function(t,e){},578:function(t,e){},579:function(t,e){},580:function(t,e){},581:function(t,e){},582:function(t,e){},583:function(t,e){},584:function(t,e){},585:function(t,e){},586:function(t,e){},587:function(t,e){},591:function(t,e,n){function i(t){n(585)}var a=n(8)(n(227),n(609),i,null,null);t.exports=a.exports},592:function(t,e,n){function i(t){n(580)}var a=n(8)(n(228),n(604),i,null,null);t.exports=a.exports},593:function(t,e,n){function i(t){n(584)}var a=n(8)(n(229),n(608),i,null,null);t.exports=a.exports},594:function(t,e,n){function i(t){n(579)}var a=n(8)(n(230),n(603),i,null,null);t.exports=a.exports},595:function(t,e,n){function i(t){n(577)}var a=n(8)(n(231),n(601),i,null,null);t.exports=a.exports},596:function(t,e,n){function i(t){n(582)}var a=n(8)(n(232),n(606),i,null,null);t.exports=a.exports},597:function(t,e,n){function i(t){n(587)}var a=n(8)(n(233),n(611),i,null,null);t.exports=a.exports},598:function(t,e,n){function i(t){n(583)}var a=n(8)(n(234),n(607),i,null,null);t.exports=a.exports},599:function(t,e,n){function i(t){n(581)}var a=n(8)(n(235),n(605),i,null,null);t.exports=a.exports},600:function(t,e,n){function i(t){n(586)}var a=n(8)(n(236),n(610),i,null,null);t.exports=a.exports},601:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[n("h1",[t._v("Simulations")]),t._v(" "),t.loading?n("div",[t._v("Loading...")]):t._e(),t._v(" "),t.loading?t._e():n("ul",{staticClass:"linklist"},t._l(t.simulations,function(e){return n("li",[n("router-link",{attrs:{to:{name:"simulation",query:{sim:e.path}}}},[t._v(t._s(e.owner+" / "+e.path))])],1)}),0)])},staticRenderFns:[]}},602:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{attrs:{id:"app"}},[n("ul",{staticClass:"homenav"},[n("li",[n("router-link",{attrs:{to:{name:"home"}}},[t._v("Simulations")])],1)]),t._v(" "),n("router-view")],1)},staticRenderFns:[]}},603:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[t.loading?n("div",[t._v("\n  Loading firm ranking...\n")]):t._e(),t._v(" "),t.loading?t._e():n("div",[n("p",[t._v("Ranking of firm types by the realized total gains their consumer-shareholders made. The gains include dividends and realized capital gains.")]),t._v(" "),n("table",{staticClass:"agentlist"},[t._m(0),t._v(" "),t._l(t.ranking,function(e,i){return n("tr",[n("td",[t._v(t._s(i+1))]),t._v(" "),n("td",[t._v(t._s(""+e.type))]),t._v(" "),n("td",[t._v(t._s(""+e.score))]),t._v(" "),n("td",[n("a",{attrs:{href:""+e.url}},[t._v("source")])]),t._v(" "),n("td",[t._v(t._s(""+e.version))])])})],2)])])},staticRenderFns:[function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("tr",[n("td",[t._v("Rank")]),t._v(" "),n("td",[t._v("Firm")]),t._v(" "),n("td",[t._v("Gains")]),t._v(" "),n("td",[t._v("Source")]),t._v(" "),n("td",[t._v("Version")])])}]}},604:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{class:{context:!0,childselection:!0,in:t.show},attrs:{id:"childselection"}},[n("h2",{staticClass:"childselection__title"},[t._v(t._s(t.childrenof)+" on day "+t._s(t.simulationday))]),t._v(" "),t.loading?n("div",[t._v("Loading ...")]):t._e(),t._v(" "),t.loading?t._e():n("div",[n("label",[n("input",{directives:[{name:"model",rawName:"v-model",value:t.allSelected,expression:"allSelected"}],attrs:{type:"checkbox"},domProps:{checked:Array.isArray(t.allSelected)?t._i(t.allSelected,null)>-1:t.allSelected},on:{change:function(e){var n=t.allSelected,i=e.target,a=!!i.checked;if(Array.isArray(n)){var r=t._i(n,null);i.checked?r<0&&(t.allSelected=n.concat([null])):r>-1&&(t.allSelected=n.slice(0,r).concat(n.slice(r+1)))}else t.allSelected=a}}}),t._v(" all")]),t._v(" "),n("ul",{staticClass:"nolist childselection__list"},t._l(t.children,function(e){return n("li",{staticClass:"childselection__item"},[n("label",[n("input",{directives:[{name:"model",rawName:"v-model",value:t.activeChildren,expression:"activeChildren"}],attrs:{type:"checkbox"},domProps:{value:e,checked:Array.isArray(t.activeChildren)?t._i(t.activeChildren,e)>-1:t.activeChildren},on:{change:function(n){var i=t.activeChildren,a=n.target,r=!!a.checked;if(Array.isArray(i)){var s=e,o=t._i(i,s);a.checked?o<0&&(t.activeChildren=i.concat([s])):o>-1&&(t.activeChildren=i.slice(0,o).concat(i.slice(o+1)))}else t.activeChildren=r}}}),t._v(" "+t._s(e))])])}),0)]),t._v(" "),t.loading?t._e():n("el-button",{staticClass:"childselection__btn",on:{click:t.saveSelection}},[t._v("Save")]),t._v(" "),n("el-button",{staticClass:"childselection__btn",on:{click:t.cancelSelection}},[t._v("Cancel")])],1)},staticRenderFns:[]}},605:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[n("svg",{staticClass:"tradegraph",attrs:{id:"stage",xmlns:"http://www.w3.org/2000/svg"}}),t._v(" "),n("div",{class:{context:!0,contextmenu:!0,in:t.showContext},style:{left:t.contextLeft+"px",top:t.contextTop+"px"},attrs:{id:"contextmenu"}},[n("ul",{staticClass:"contextmenu__list"},[n("li",{staticClass:"contextmenu__item"},[n("el-button",{staticClass:"contextmenu__btn",attrs:{id:"minichartselection"}},[t._v("Add Minichart")])],1),t._v(" "),n("li",{staticClass:"contextmenu__item"},[n("el-button",{staticClass:"contextmenu__btn",attrs:{id:"infoselection"}},[t._v("Show Info")])],1),t._v(" "),n("li",{staticClass:"contextmenu__item"},[n("el-button",{staticClass:"contextmenu__btn",attrs:{id:"childrenselection"}},[t._v("Expand/Collapse Children")])],1)])])])},staticRenderFns:[]}},606:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[t._l(t.agents,function(e){return[e?n("div",{key:"minichart-"+e.id,staticClass:"minichart__wrapper"},[n("div",{staticClass:"minichart__title"},[t._v(t._s(t.graphTitle[e.id]))]),t._v(" "),n("div",{staticClass:"minichart",attrs:{id:"minichart-"+e.id}})]):t._e()]})],2)},staticRenderFns:[]}},607:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[n("h1",[t._v("Simulation '"+t._s(this.$route.query.sim)+"'")]),t._v(" "),t.loading?n("div",[t._v("Loading...")]):t._e(),t._v(" "),t.loading?t._e():n("div",[t._v("This simulation is based on the\n    "),n("a",{attrs:{href:t.simInfo.configurationSourceURL}},[t._v(t._s(t.simInfo.configurationName)+" configuration")]),t._v(" and runs for "+t._s(t.simInfo.days)+" days.")]),t._v(" "),n("h2",[t._v("Ranking")]),t._v(" "),n("consumerranking",{attrs:{simulationid:t.simId}}),t._v(" "),n("firmranking",{attrs:{simulationid:t.simId}}),t._v(" "),n("h2",[t._v("Visualization")]),t._v(" "),t.loading?t._e():n("ul",{staticClass:"linklist"},[n("li",[n("router-link",{attrs:{to:{name:"trades",query:{sim:this.$route.query.sim,day:0,selection:"consumers,firms",step:1}}}},[t._v("Trade")])],1)]),t._v(" "),n("h2",[t._v("Data")]),t._v(" "),n("div",{staticClass:"chart-wrapper"},[n("chart",{attrs:{simulationid:t.simId,suggestedmetric:t.datametric},on:{updateUrl:t.updateUrl}})],1)],1)},staticRenderFns:[]}},608:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",[t.loading?n("div",[t._v("\n  Loading consumer ranking...\n")]):t._e(),t._v(" "),t.loading?t._e():n("div",[n("p",[t._v("For configurations with immortal consumers, the consumer ranking is based on an exponentially moving average, measured at the last day of the simulation. If there are multiple instances of an agent type, the type score is the average score of its instances. For simulations with mortal consumers, the type score is based on the average life-time utility of all instances that died in the second half of the simulation.")]),t._v(" "),t.loading?t._e():n("table",{staticClass:"agentlist"},[t._m(0),t._v(" "),t._l(t.ranking,function(e,i){return n("tr",[n("td",[t._v(t._s(i+1))]),t._v(" "),n("td",[t._v(t._s(""+e.type))]),t._v(" "),n("td",[t._v(t._s(""+e.score))]),t._v(" "),n("td",[n("a",{attrs:{href:""+e.url}},[t._v("source")])]),t._v(" "),n("td",[t._v(t._s(""+e.version))])])})],2)])])},staticRenderFns:[function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("tr",[n("td",[t._v("Rank")]),t._v(" "),n("td",[t._v("Consumer")]),t._v(" "),n("td",[t._v("Utility")]),t._v(" "),n("td",[t._v("Source")]),t._v(" "),n("td",[t._v("Version")])])}]}},609:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"chart__wrapper"},[t._v("\n  Metric:\n  "),n("el-dropdown",{attrs:{trigger:"click"},on:{command:t.updateMetric}},[n("el-button",{attrs:{type:"primary"}},[t._v(t._s(""+t.suggestedmetric)),n("i",{staticClass:"el-icon-caret-bottom el-icon--right"})]),t._v(" "),n("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[t._l(t.metrics,function(e){return[n("el-dropdown-item",{attrs:{command:e}},[t._v(t._s(e))])]})],2)],1),t._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:t.download}},[t._v("Download")]),t._v(" "),n("p",[t._v(t._s(""+t.description))]),t._v(" "),t._l(t.selectedSeries,function(e,i){return[n("el-dropdown",{attrs:{trigger:"click"},on:{command:t.updateSeries}},[n("el-button",[t._v(t._s(""+e)),n("i",{staticClass:"el-icon-caret-bottom el-icon--right"})]),t._v(" "),n("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[t._l(t.series,function(e){return[n("el-dropdown-item",{attrs:{command:[i,e]}},[t._v(t._s(e))])]})],2)],1)]}),t._v(" "),t.renderRefresh?n("el-button",{on:{click:t.updateChart}},[t._v("Refresh")]):t._e(),t._v(" "),t.renderRemove?n("el-button",{on:{click:t.removeSeries}},[t._v("Remove")]):t._e(),t._v(" "),t.renderAdd?n("el-button",{on:{click:t.addSeries}},[t._v("Add")]):t._e(),t._v(" "),n("div",{staticClass:"chart",attrs:{id:"chart"}})],2)},staticRenderFns:[]}},610:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"tradeview"},[n("h1",[t._v("Tradeview")]),t._v(" "),n("p",[t._v("This view visualizes the trading of goods between consumers and firms in a sequence economy.")]),t._v(" "),t.loaded?n("div",{staticClass:"tradeview__wrapper"},[n("div",{staticClass:"tradeview__main"},[n("div",{staticClass:"controls"},[t.simDay<t.simLength?n("el-button",{attrs:{type:"primary"},on:{click:function(e){t.playing=!t.playing}}},[t.playing?n("svg",{attrs:{width:"9",height:"9",viewBox:"0 0 6 9",xmlns:"http://www.w3.org/2000/svg"}},[n("path",{attrs:{fill:"#fff",d:"M0 0h2v9H0zM4 0h2v9H4z"}})]):t._e(),t._v(" "),t.playing?t._e():n("svg",{attrs:{width:"9",height:"9",viewBox:"0 0 9 9",xmlns:"http://www.w3.org/2000/svg"}},[n("path",{attrs:{fill:"#fff",d:"M.986 0v8.935L9 4.077z"}})])]):t._e(),t._v(" "),n("el-slider",{attrs:{max:t.simLength,step:t.simStep,"show-input":""},model:{value:t.simDay,callback:function(e){t.simDay=e},expression:"simDay"}}),t._v(" "),n("el-select",{model:{value:t.simStep,callback:function(e){t.simStep=e},expression:"simStep"}},t._l(t.config.stepSizeOptions,function(t){return n("el-option",{key:t,attrs:{value:t}})}),1)],1),t._v(" "),n("tradegraph",{staticClass:"tradeview__tradechart",attrs:{graphdata:t.tradeGraphData,selectednode:t.selectedNode},on:{simstopped:t.handleSimStopped,nodeclicked:t.handleNodeClicked,addminichart:t.handleAddMinichart,showinfo:t.handleShowInfo,showchildren:t.handleShowChildSelection,hidecontextmenus:t.hideContextMenus}})],1),t._v(" "),n("div",{staticClass:"tradeview__side"},[n("div",{staticClass:"tradeview__minicharts"},[n("div",{staticClass:"tradeview__minicharts-wrapper"},[n("minicharts",{attrs:{agents:t.miniCharts,simulationday:t.simDay,simulationid:t.simId}})],1)])]),t._v(" "),n("childselection",{style:{left:t.childSelectionLeft+"px",top:t.childSelectionTop+"px"},attrs:{show:t.showChildSelection,childrenof:t.childrenOf,activenodes:t.simAgents,simulationday:t.simDay,simulationid:t.simId},on:{"update:show":function(e){t.showChildSelection=e},setactivenodes:t.handleSetActiveNodes}}),t._v(" "),n("nodeinfo",{style:{left:t.infoLeft+"px",top:t.infoTop+"px"},attrs:{show:t.showNodeInfo,agent:t.infoOf,simulationday:t.simDay,simulationid:t.simId},on:{"update:show":function(e){t.showNodeInfo=e}}})],1):t._e()])},staticRenderFns:[]}},611:function(t,e){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{class:{context:!0,nodeinfo:!0,in:t.show},attrs:{id:"nodeinfo"}},[n("h2",{staticClass:"nodeinfo__title"},[t._v(t._s(t.agent)+" on day "+t._s(t.simulationday))]),t._v(" "),t.loading?n("div",[t._v("Loading ...")]):t._e(),t._v(" "),t.loading?t._e():n("div",[n("table",[n("tr",[n("td",[t._v("Source")]),t._v(" "),n("td",[n("a",{attrs:{href:t.info.source.codeLink,target:"_blank"}},[t._v(t._s(t.info.source.owner))])])]),t._v(" "),n("tr",{directives:[{name:"show",rawName:"v-show",value:t.info.currentUtility,expression:"info.currentUtility"}]},[n("td",[t._v("Current Utility")]),t._v(" "),n("td",[t._v(t._s(t.info.currentUtility))])]),t._v(" "),n("tr",{directives:[{name:"show",rawName:"v-show",value:t.info.averageUtility,expression:"info.averageUtility"}]},[n("td",[t._v("Average Utility")]),t._v(" "),n("td",[t._v(t._s(t.info.averageUtility))])]),t._v(" "),n("tr",{directives:[{name:"show",rawName:"v-show",value:t.info.averageDividends,expression:"info.averageDividends"}]},[n("td",[t._v("Average Dividends")]),t._v(" "),n("td",[t._v(t._s(t.info.averageDividends))])]),t._v(" "),n("tr",{directives:[{name:"show",rawName:"v-show",value:t.info.totalDividends,expression:"info.totalDividends"}]},[n("td",[t._v("Total Dividends")]),t._v(" "),n("td",[t._v(t._s(t.info.totalDividends))])]),t._v(" "),n("tr",[n("td",[t._v("No of Agents")]),t._v(" "),n("td",[t._v(t._s(t.info.agents))])]),t._v(" "),t._m(0),t._v(" "),t._l(t.info.inventory,function(e){return n("tr",[n("td",[t._v(t._s(e[0]))]),t._v(" "),n("td",[t._v(t._s(e[1]))])])})],2)]),t._v(" "),n("el-button",{staticClass:"nodeinfo__btn",on:{click:t.closeInfo}},[t._v("Close")])],1)},staticRenderFns:[function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("tr",[n("th",{attrs:{colspan:"2"}},[t._v("Inventory")])])}]}}},[225]);