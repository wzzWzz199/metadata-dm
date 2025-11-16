/**
 * Created by huangjie on 2020-06-15
 */

// 表单
window.form = function(container, options) {
    this._constructor(container, options);
}
form.prototype._constructor = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'exportform');
    this._container.appendChild(this._root);

    this._currentpage = 1;

    this._root.innerHTML = "";
    var content = [
        '<div class="title clearfix">',
            '<div class="backbtn"><span class="iconfont icon-houtui"></span></div>',
            '<div class="searcharea clearfix">',
                '<label></label>',
                '<input type="text" value="" placeholder="请输入编码">',
                '<span>搜索</span>',
            '</div>',
        '</div>',
        '<div class="tablecon clearfix">',
            '<div class="original"></div>',
            '<div class="linked"></div>',
        '</div>'
    ].join('');
    this._root.innerHTML = content;

    var addtolistproc = function(data) { // 添加到导出列表
        if (this._options && this._options.addtolistproc) {
            this._options.addtolistproc(data);
        }
    }.bind(this);
    var pageclickproc = function(data) { // 分页
        if (this._options && this._options.pageclickproc) {
            this._options.searchproc({searchdata:this._lastsearchdata, page:data.page + 1, type:this._data.type});
        }
    }.bind(this);
    var exportproc = function(data) { // 导出
        if (this._options && this._options.exportproc) {
            this._options.exportproc(data);
        }
    }.bind(this);
    var removeproc = function(data) {
        if (this._options && this._options.removeproc) {
            this._options.removeproc(data);
        }
    }.bind(this);

    // 创建original表头（右侧）
    this._original = new original(this._root.childNodes[1].childNodes[0], {exportproc:exportproc, addtolistproc:addtolistproc, pageclickproc:pageclickproc});
    // 创建linked表头（左侧）
    this._linked = new linked(this._root.childNodes[1].childNodes[1], {exportproc:exportproc, removeproc:removeproc});
}
form.prototype.showpage = function(page, total) {
    this._original.showpage(page, total);
}
form.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
form.prototype.layout = function() {
    var returnclick = function(e) { // 返回
        if (this._options && this._options.returnproc) {
            this._root.childNodes[0].childNodes[1].childNodes[1].value = '';
            this._options.returnproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var searchproc = function(e) { // 查询
        if (this._options && this._options.searchproc) {
            this._lastsearchdata = this._root.childNodes[0].childNodes[1].childNodes[1].value;
            this._options.searchproc({searchdata:this._lastsearchdata, page:1, type:this._data.type});
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var keydownproc = function(e) {
        if (e.keyCode == 13) {
            if (this._options && this._options.searchproc) {
                this._lastsearchdata = this._root.childNodes[0].childNodes[1].childNodes[1].value;
                this._options.searchproc({searchdata:this._lastsearchdata, page:1, type:this._data.type});
            }
        }
        e.stopImmediatePropagation();
    }.bind(this);

    var data = this._data;
    this._root.childNodes[0].childNodes[1].childNodes[0].innerHTML = data.name;
    this._root.childNodes[0].childNodes[1].childNodes[1].setAttribute('placeholder', '请输入' + data.queryItemName);

    this._root.childNodes[0].childNodes[0].addEventListener("click", returnclick);
    this._root.childNodes[0].childNodes[1].childNodes[2].addEventListener("click", searchproc);
    this._root.childNodes[0].childNodes[1].childNodes[1].addEventListener("keydown", keydownproc);

    if(data.hidesearch) {
        addclass(this._root, 'hidesearch');
    } else {
        removeclass(this._root, 'hidesearch');
    }

    // 加载original表头数据
    this._original.data(data);

    // 加载linked表头数据
    this._linked.data(data);

    if (data.isappexport == 1) {
        addclass(this._root, 'isappexport');
    } else {
        removeclass(this._root, 'isappexport');
    }
}
// 渲染original表格的数据
form.prototype.originaldata = function(data) {
    this._original.tabledata(data);
}
form.prototype.setoriginalstatus = function(data, exported) {
    this._original.setdatastatus(data, exported);
}
form.prototype.updateoriginalstatus = function(datas) {
    this._original.updatedatastatus(datas);
}
form.prototype.adddata = function(data) {
    this._linked.adddata(data);
}
form.prototype.selecteddatas = function() {
    return this._linked.tabledata();
}
