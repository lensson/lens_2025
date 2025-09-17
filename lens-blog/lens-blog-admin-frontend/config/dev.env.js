'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')


module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',


  //开发环境
  ADMIN_API:  '"/api/v1/blog/admin"',
  PICTURE_API: '"/api/v1/blog/picture"',
  WEB_API:  '"/api/v1/blog/web"',
  Search_API: '"/api/v1/blog/search"',
  Spider_API: '"/api/v1/blog/spider"',
  FILE_API: '"http://localhost:8600/"',
  BLOG_WEB_URL: '"http://localhost:8002"',
  SOLR_API: '"http://localhost:8080/solr"',
  ELASTIC_SEARCH: '"http://localhost:5601"',
})
