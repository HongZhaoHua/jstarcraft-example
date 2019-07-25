// 配置
var apiDomain = 'http://localhost:8081'; // api域名
var apiUrl = {
  recommend: apiDomain + '/recommend', // 推荐api地址
  search: apiDomain + '/search', // 搜索api地址
};

var Status = {
  ready: 'ready', // 初始化
  loading: 'loading', // 加载中
  success: 'success', // 成功
  error: 'error', // 失败
};


new Vue({
  el: '#container',
  data: {
    isInit:false, // 是否已初始化,防止一进来看到页面乱码
    // 推荐
    recommend: {
      data: [],
      status: Status.loading
    },
    keyword:'', // 搜索关键字
    // 搜索结果
    result: {
      data: [],
      status: Status.loading
    }
  },
  mounted:function () {
    this.isInit=true;
    this.getRecommend();
    this.search();
  },
  methods: {
    // 获取推荐
    getRecommend: function () {
      this.recommend.status=Status.loading;
      var me=this;
      $.ajax({
        method: "POST",
        url: apiUrl.recommend,
        dataType:"json",
        data: {}
      }).done(function (res) {
        me.recommend.data=res.data;
        me.recommend.status=Status.success;
      }).fail(function() {
        me.recommend.status=Status.error;
      });
    },
    // 开始搜索
    search: function () {
      this.result.status=Status.loading;
      var me=this;
      $.ajax({
        method: "POST",
        url: apiUrl.search,
        dataType:"json",
        data: {
          keyword:this.keyword
        }
      }).done(function (res) {
        me.result.data=res.data;
        me.result.status=Status.success;
      }).fail(function() {
        me.result.status=Status.error;
      });
    }
  }
});