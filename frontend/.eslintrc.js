// http://eslint.org/docs/user-guide/configuring

module.exports = {
  root: true,
  parserOptions: {
    parser: 'babel-eslint',
    sourceType: 'module'
  },
  env: {
    browser: true,
  },
  extends: ['plugin:vue/base'], 
  // required to lint *.vue files
  plugins: [
    'vue'
  ],
  // check if imports actually resolve
  'settings': {
    'import/resolver': {
      'webpack': {
        'config': 'build/webpack.base.conf.js'
      }
    }
  },
  // add your custom rules here
  'rules': {
    "linebreak-style": 0,
    // allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0,
    // no param reassign
    'no-param-reassign': ['error', {
      'props': true, 'ignorePropertyModificationsFor': ['d', 'graph']
    }],
    // no mixed operators
    'no-mixed-operators': ['error', {
      'allowSamePrecedence': true
    }],
  }
}
