// 配置
var apiDomain = ''; // api域名
var apiUrl = {
    users: apiDomain + '/movies/getUsers', // 获取用户api地址
    items: apiDomain + '/movies/getItems', // 获取条目api地址
    click: apiDomain + '/movies/click', // 点击api地址
};

var pageSize = 10; // 每页显示多少条目
var columns = 5; // 每行显示多少条目

var modelKeys = [
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

var filterKeys = [
    {
        name: '是',
        value: true,
    }, {
        name: '否',
        value: false,
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
        modelKeys: {
            isShow: false,  // 是否显示算法下拉列表
            content: modelKeys,
            index: -1
        },
        // 查询
        queryKey: '', // 查询关键字
        filterKeys: {
            isShow: false,  // 是否显示算法下拉列表
            content: filterKeys,
            index: -1
        },
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
                element.modelKeys.isShow = false;
                element.filterKeys.isShow = false;
                element.users.isShow = false;
            });
        },
        // 点击
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
        // 获取物品
        getItems: function () {
            var element = this;
            // 请求参数
            var request = {};
            if (this.users.index !== -1) {
                request.userIndex = this.users.content[this.users.index].id;
            }
            // 推荐
            request.modelKey = this.modelKeys.content[this.modelKeys.index].value;
            // 搜索
            request.queryKey = this.queryKey;
            request.filterClicked =  this.filterKeys.content[this.filterKeys.index].value
            var query = {
                method: "GET",
                url: apiUrl.items,
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
        showModelKeys: function () {
            this.modelKeys.isShow = true;
        },
        // 选择(算法)
        selectRecommendKey: function (index) {
            this.modelKeys.index = index;
        },
        // 显示下拉框(算法)
        showFilterKeys: function () {
            this.filterKeys.isShow = true;
        },
        // 选择(算法)
        selectFilterKey: function (index) {
            this.filterKeys.index = index;
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