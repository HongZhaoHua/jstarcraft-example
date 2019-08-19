// 配置
var apiDomain = ''; // api域名
var apiUrl = {
    users: apiDomain + '/movies/getUsers', // 获取用户api地址
    recommendItems: apiDomain + '/movies/getRecommendItems', // 推荐api地址
    searchItems: apiDomain + '/movies/getSearchItems', // 搜索api地址
    click: apiDomain + '/movies/click', // 点击api地址
};

var pageSize = 10; // 每页显示多少部电影
var columns = 5; // 一列显示多少部电影

var recommendKeys = [
    {
        name: 'AssociationRule',
        value: 'AssociationRule',
    }, {
        name: 'BPR',
        value: 'BPR',
    }, {
        name: 'ItemKNN',
        value: 'ItemKNN',
    }, {
        name: 'LDA',
        value: 'LDA',
    }, {
        name: 'MostPopular',
        value: 'MostPopular',
    }, {
        name: 'Random',
        value: 'Random',
    }, {
        name: 'UserKNN',
        value: 'UserKNN',
    }, {
        name: 'WRMF',
        value: 'WRMF',
    }
];


var state = {
    load: 'load', // 加载中
    normal: 'normal', // 成功
    abnormal: 'abnormal', // 失败
};

var cache = []; // 缓存数据,用于前端分页

new Vue({
    el: '#container',
    data: {
        isShow: false, // 是否已初始化,防止一进来看到页面乱码
        type: 'search', // 类型(search:搜索 recommend:推荐)
        columns: columns, // 一列显示多少部电影
        users: {
            isShow: false,  // 是否显示用户下拉列表
            content: [],
            index: -1
        },
        recommendKeys: {
            isShow: false,  // 是否显示算法下拉列表
            content: recommendKeys,
            index: -1
        },
        // 推荐
        searchKey: '', // 搜索关键字
        // 搜索结果
        data: {
            pageIndex: 1, // 当前是第几页
            pageCount: 1, // 总共有多少页
            content: [], // 数据
            style: {},
            status: state.load
        }
    },
    mounted: function () {
        // 获取页面链接
        this.initialize();
    },
    methods: {
        // 初始化
        initialize: function () {
            var element = this;
            // 获取链接参数
            var parameters = this.getParameters(location.search);
            if (parameters && parameters.type) {
                this.type = parameters.type;
            }
            this.isShow = true;
            this.getUsers();
            document.addEventListener('click', function (event) {
                var className = event.target.getAttribute('class');
                if (className && className.indexOf('dropdown-toggle') !== -1) {
                    return;
                }
                element.recommendKeys.isShow = false;
                element.users.isShow = false;
            });
        },
        // 点击(电影)
        click: function (itemId, score) {
            var data = {};
            if (this.users.index !== -1) {
                data.userIndex = this.users.content[this.users.index].id;
            }
            data.itemIndex = itemId;
            data.score = score;
            var query = {
                method: "GET",
                url: apiUrl.click,
                dataType: "json",
                data: data
            };
            $.ajax(query);
        },
        // 获取用户
        getUsers: function () {
            var element = this;
            // 请求参数
            var data = {};
            var query = {
                method: "GET",
                url: apiUrl.users,
                dataType: "json",
                data: data
            };
            $.ajax(query).done(function (data) {
                element.users.content = data.content;
            }).fail(function () {
            });
        },
        // 获取物品(电影)
        getItems: function () {
            var element = this;

            // 请求参数
            var request;
            if (this.type === 'recommend') {
                // 判断是否选择了算法
                if (this.recommendKeys.index === -1) {
                    alert('请先选择推荐算法');
                    return;
                }
                // 推荐
                request = {
                    recommendKey: this.recommendKeys.content[this.recommendKeys.index].value
                };
            } else {
                // 搜索
                request = {
                    searchKey: this.searchKey
                };
            }
            if (this.users.index !== -1) {
                request.userIndex = this.users.content[this.users.index].id;
            }
            var query = {
                method: "GET",
                url: this.type === 'search' ? apiUrl.searchItems : apiUrl.recommendItems,
                dataType: "json",
                data: request
            };

            var response = this.data;
            response.status = state.load;
            response.pageIndex = 1;
            response.content = [];
            response.style = {};
            $.ajax(query).done(function (data) {
                cache = data.content;
                element.data.pageCount = Math.ceil(cache.length / pageSize);
                element.showPage(response.pageIndex);
                element.data.status = state.normal;
            }).fail(function () {
                element.data.status = state.abnormal;
            });
        },
        // 显示下拉框(算法)
        showRecommendKeys: function () {
            this.recommendKeys.isShow = true;
        },
        // 选择(算法)
        selectRecommendKey: function (index) {
            this.recommendKeys.index = index;
        },
        // 显示下拉框(用户)
        showUsers: function () {
            this.users.isShow = true;
        },
        // 选择(用户)
        selectUser: function (index) {
            this.users.index = index;
        },
        getParameters: function (query) {
            if (query) {
                var index = 0;
                if (query.indexOf('?') !== -1) {
                    index = 1;
                }
                var parameters = {};
                query.substr(index).split('&').forEach(parameter => {
                    var keyValue = parameter.split('=');
                    parameters[keyValue[0]] = keyValue[1];
                });
                return parameters;
            }
            return null;
        },
        // 显示对应的页数
        showPage: function (pageIndex) {
            // 取第几页显示
            var length = this.data.content.length;
            if (length < pageIndex) {
                var array = cache.slice((pageIndex - 1) * pageSize, (pageIndex - 1) * pageSize + pageSize);
                this.data.content.push(array);
            }
            this.data.pageIndex = pageIndex;
            // 计算位移
            var delta = -(pageIndex - 1) * 100;
            var translate = `translate(${delta}%,0)`;
            var style = {
                transform: translate,
                webkitTransform: translate,
            };
            this.data.style = style;
        },
        // 上一页
        previousPage: function () {
            if (this.data.pageIndex > 1) {
                this.showPage(this.data.pageIndex - 1);
            }
        },
        // 下一页
        nextPage: function () {
            if (this.data.pageIndex < this.data.pageCount) {
                this.showPage(this.data.pageIndex + 1);
            }
        }
    }
});