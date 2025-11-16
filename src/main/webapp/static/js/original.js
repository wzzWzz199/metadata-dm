/**
 * Created by huangjie on 2020-06-16
 */

// 左侧表格
window.original = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'tablewrap');
    this._container.appendChild(this._root);
}
original.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data || null;
    }
}
original.prototype.layout = function() {
    var returnclick = function(e) { // 返回
        if (this._options && this._options.returnproc) {
            this._options.returnproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var pageclickproc = function(data) {  // pageclickproc这里的名字是可以随便起的，是在这个函数内部使用
        if (this._options && this._options.pageclickproc) {  // 这里的pageclickproc是要传出去的，传出去时要统一
            this._options.pageclickproc(data);
        }
    }.bind(this);

    this._root.innerHTML = "";
    if (this._data) {
        var data = this._data;
        var listItemName = data.listItemName.split(',');

        var content = [
            '<table>',
                '<colgroup><col width="300"><col width="300"><col width="110"></colgroup>',
                '<thead><tr><th>' + listItemName[0] + '</th><th>' + listItemName[1] + '</th><th>操作</th></tr></thead>',
                '<tbody></tbody>',
            '</table>',
            '<div class="page"></div>'
        ].join('');
    }

    this._root.innerHTML = content;

    this._datacontainer = this._root.childNodes[0].childNodes[2];
    this._pagecontainer = this._root.childNodes[1];

    this._page = new flipper(this._pagecontainer, {pageclickproc:pageclickproc});
}
original.prototype.showpage = function(page, total) {
    this._page.layoutstatus(page, total);
}
original.prototype.tabledata = function(value) {
    if (value !== undefined) {
        this._tabledata = value;
        this.tablelayout();
    } else {
        return this._tabledata || null;
    }
}
original.prototype.tablelayout = function() {
    var addtolistproc = function(e) {
        if (this._options && this._options.addtolistproc) {
            var code = e.currentTarget.getAttribute('name');
            for (var i = 0; i < data.length; i++) {
                if (data[i].metaDataCode == code) {
                    if (data[i].isappexport == 1) {
                        this._options.exportproc([data[i]]);
                    } else {
                        this._options.addtolistproc(data[i]);
                    }
                    break;
                }
            }
        }
        e.stopImmediatePropagation();
    }.bind(this);

    var data = this._tabledata.voList;
    var page = this._tabledata.page;
    var str = '';
    var con = '';
    for (var i = 0; i < data.length; i++) {
        if (data[i].isappexport == 1) {
            con = '<td class="addtolist"><span name="' + data[i].metaDataCode + '">导出</span></td>';
        } else {
            con = '<td class="addtolist"><span name="' + data[i].metaDataCode + '">添加导出</span></td>';
        }
        str += '<tr>' +
            '<td><span class="datacode">' + data[i].metaDataCode + '</span></td>' +
            '<td class="titlename">' + data[i].metaDataName + '</td>' +
            con +
            // '<td class="addtolist"><span name="' + data[i].metaDataCode + '">添加导出</span></td>' +
            '</tr>';
    }
    this._datacontainer.innerHTML = str;

    // 点击添加导出
    var tr = this._datacontainer.childNodes;
    console.log('tr-', tr);
    for (var i = 0; i < tr.length; i++) {
        tr[i].childNodes[2].childNodes[0].addEventListener('click', addtolistproc);
    }
}
original.prototype.findindex = function(code) {
    for (var i = 0; i < this._tabledata.voList.length; i++) {
        var data = this._tabledata.voList[i];
        if (data.metaDataCode == code) {
            return i;
        }
    }
    return -1;
}
original.prototype.setdatastatus = function(data, exported) {
    var index = this.findindex(data.metaDataCode);
    if (index < 0) return;
    if (exported) addclass(this._datacontainer.children[index], 'exported');
    else removeclass(this._datacontainer.children[index], 'exported');
}
original.prototype.updatedatastatus = function(datas) {
    function find(data) {
        for (var i = 0; i < datas.length; i++) {
            if (datas[i].metaDataCode == data.metaDataCode) return true;
        }
        return false;
    }
    for (var i = 0; i < this._tabledata.voList.length; i++) {
        if (find(this._tabledata.voList[i])) addclass(this._datacontainer.children[i], 'exported');
        else removeclass(this._datacontainer.children[i], 'exported');
    }
}

