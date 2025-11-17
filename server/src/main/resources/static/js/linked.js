/**
 * Created by huangjie on 2020-06-16
 */

// 右侧表格
window.linked = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'tablewrap');
    this._container.appendChild(this._root);
    this._tabledata = [];
}
linked.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
linked.prototype.layout = function() {
    var returnclick = function(e) { // 返回
        if (this._options && this._options.returnproc) {
            this._options.returnproc(this._data);
        }
        e.stopImmediatePropagation();
    }.bind(this);
    var exportproc = function(data) { // 导出
        if (this._options && this._options.exportproc) {
            this._options.exportproc(this._tabledata);
        }
    }.bind(this);

    this._root.innerHTML = "";
    var data = this._data;
    var listItemName = data.listItemName.split(',');

    var str = '<th>' + listItemName[0] + '</th>';
    str += '<th class="titlename">' + listItemName[1] + '</th>';
    var col = '';
    for (var i = 0; i < data.linkedType.length; i++) {
        var linkdata = data.linkedType[i];
        str += '<th class="linkedtype" name="' + linkdata.linkedtype + '">' + linkdata.name + '</th>';
        col += '<col width="150">';
    }
    var content = [
        '<table>',
            '<colgroup><col width="300"><col width="300">' + col + '<col width="80"></colgroup>',
            '<thead><tr>' + str + '<th class="delete">删除</th></tr></thead>',
            '<tbody></tbody>',
        '</table>',
        // '<div class="linkedwrap"></div>' +
        '<div class="exportbtn">导出</div>'
    ].join('');

    this._root.innerHTML = content;
    this._datacontainer = this._root.childNodes[0].childNodes[2];

    this._root.childNodes[1].addEventListener('click', exportproc);

    this.tabledata([]);
}
linked.prototype.tabledata = function(value) {
    if (value !== undefined) {
        this._tabledata = value;
        this.tablelayout();
    } else {
        return this._tabledata || null;
    }
}
linked.prototype.findindex = function(code) {
    for (var i = 0; i < this._tabledata.length; i++) {
        var data = this._tabledata[i];
        if (data.metaDataCode == code)  return i;
    }
    return -1;
}
linked.prototype.adddata = function(tabledata) {
    var data = {metaDataCode:tabledata.metaDataCode, metaDataLinkType:tabledata.metaDataLinkType, metaDataName:tabledata.metaDataName, metaDataType:tabledata.metaDataType};
    if (this.findindex(data.metaDataCode) >= 0) return;
    this._tabledata.push(data);
    var strtype = '';
    var linkedtype = this._data.linkedType;
    for (var j = 0; j < linkedtype.length; j++) {
        strtype += '<td class="linkedtype uncheck" rowcode="' + data.metaDataCode + '" linktype="' + linkedtype[j].linkedtype + '"></td>';
    }
    var str = ('<tr>' +
        '<td><span class="datacode">' + data.metaDataCode + '</span></td>' +
        '<td>' + data.metaDataName + '</td>' +
        strtype +
        '<td class="deletebtn" rowcode="' + data.metaDataCode + '"></td>' +
        '</tr>');
    this._datacontainer.innerHTML += str;
    this.addallevent();
}
linked.prototype.removedata = function(code) {
    var index = this.findindex(code);
    if (index < 0) return;
    var data = this._tabledata[index];
    this._tabledata.splice(index, 1);
    this._datacontainer.removeChild(this._datacontainer.children[index]);
    if (this._options && this._options.removeproc) {
        this._options.removeproc(data);
    }
}
linked.prototype.tablelayout = function() {
    var datas = this._tabledata;
    var linkedtype = this._data.linkedType;
    var str = '';
    for (var i = 0; i < datas.length; i++) {
        var data = datas[i];
        var strtype = '';
        for (var j = 0; j < linkedtype.length; j++) {
            strtype += '<td class="linkedtype uncheck" rowcode="' + data.metaDataCode + '" linktype="' + linkedtype[j].linkedtype + '"></td>';
        }
        str += ('<tr>' +
            '<td>' + data.metaDataCode + '</td>' +
            '<td>' + data.metaDataName + '</td>' +
            strtype +
            '<td><span rowcode="' + data.metaDataCode + '" name="remove" class="deletebtn"></span></td>' +
            '</tr>');
    }
    this._datacontainer.innerHTML = str;
    this.addallevent();
}
linked.prototype.addallevent = function() {
    var clickproc = function(e) {
        var code = e.currentTarget.getAttribute('rowcode');
        if (code != null) this.removedata(code);
    }.bind(this);
    var checkchangeproc = function(e) {
        var td = e.currentTarget;
        var code = td.getAttribute('rowcode');
        var type = td.getAttribute('linktype');
        var index = this.findindex(code);
        if (index < 0) return;
        var data = this._tabledata[index];
        var linktype = data.metaDataLinkType ? data.metaDataLinkType.split(',') : [];
        var linkindex = linktype.indexOf(type);
        if (linkindex >= 0) {
            linktype.splice(linkindex, 1);
            removeclass(td, 'checked');
        } else {
            linktype.push(type);
            addclass(td, 'checked');
        }
        data.metaDataLinkType = linktype.join(',');
    }.bind(this);

    for (var i = 0; i < this._datacontainer.children.length; i++) {
        var child = this._datacontainer.children[i];

        for (var j = 2; j < child.children.length - 1; j++) {
            var check = child.children[j];
            if (check) {
                check.addEventListener('click', checkchangeproc);
            }
        }

        var removebtn = child.children[child.children.length - 1];
        if (removebtn) {
            removebtn.addEventListener('click', clickproc);
        }
    }
}
linked.prototype.addtdevent = function() {
    var clickproc = function(e) {
        var code = e.currentTarget.getAttribute('rowcode');
        if (code != null) this.removedata(code);
    }.bind(this);
    var checkchangeproc = function(e) {
        var td = e.currentTarget;
        var code = td.getAttribute('rowcode');
        var type = td.getAttribute('linktype');
        var index = this.findindex(code);
        if (index < 0) return;
        var data = this._tabledata[index];
        var linktype = (data.metaDataLinkType || '').split(',');
        var linkindex = linktype.indexOf(type);
        if (linkindex >= 0) {
            linktype.splice(linkindex, 1);
            removeclass(td, 'checked');
        } else {
            linktype.push(type);
            addclass(td, 'checked');
        }
        data.metaDataLinkType = linktype.join(',');
    }
    var child = this._datacontainer.children[this._datacontainer.children.length - 1];

    for (var j = 2; j < child.children.length - 1; j++) {
        var check = child.children[j];
        if (check) {
            check.addEventListener('click', checkchangeproc);
        }
    }

    var removebtn = child.children[child.children.length - 1];
    if (removebtn) {
        removebtn.addEventListener('click', clickproc);
    }
}
