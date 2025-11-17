/**
 * Created by huangjie on 2020-06-15
 */

window.exportcon = function(container, options) {
    this._constructor(container, options);
}
exportcon.prototype._constructor = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'export');
    this._container.appendChild(this._root);

    var returnproc = function(e) { // 返回
        if (this._options && this._options.returnproc) {
            this._options.returnproc(this._data);
            this.selected(false);
        }
    }.bind(this);

    var searchproc = function(data) { // 查询
        if (this._options && this._options.searchproc) {
            this._options.searchproc(data);
        }
    }.bind(this);

    var addtolistproc = function(data) { // 添加到导出列表
        if (this._options && this._options.addtolistproc) {
            this._options.addtolistproc(data);
        }
    }.bind(this);

    var exportproc = function(data) { // 导出
        if (this._options && this._options.exportproc) {
            this._options.exportproc({metaType:this._btncode, exportdatas:data});
        }
    }.bind(this);

    var removeproc = function(data) {
        if (this._options && this._options.removeproc) {
            this._options.removeproc(data);
        }
    }.bind(this);

    var clickproc = function(data) {
        if (this._options && this._options.clickproc) {
            this._options.clickproc(data);
            this._btncode = data.type;
            this._form.data(data);
            this.selected(true);
        }
    }.bind(this);

    var pageclickproc = function(data) {
        if (this._options && this._options.pageclickproc) {
            this._options.pageclickproc(data);
        }
    }.bind(this);

    var content = [
        '<div class="tenant">海顿</div>',
        '<div class="btnwrap clearfix">' +
            '<div></div>' +
            '<div class="appbtn">应用导出</div>' +
        '</div>',
        '<div class="exportmodule"></div>',
    ].join('');
    this._root.innerHTML = content;
    this._tenantcombo = this._root.childNodes[0];
    this._btngroup = new btngroup(this._root.childNodes[1].childNodes[0], {clickproc:clickproc});

    // 创建表单（这个是点击导出部分的任意一个按钮[除应用导出]显示出来的页面）
    this._form = new form(this._root.childNodes[2], {returnproc:returnproc, searchproc:searchproc, addtolistproc:addtolistproc, exportproc:exportproc, removeproc:removeproc, pageclickproc:pageclickproc});

    // 应用导出
    var appexportproc = function() {
        if (this._options && this._options.appexportproc) {
            this._btncode = "ALL";
            this._options.appexportproc();
            this.selected(true);
        }
    }.bind(this);
    this._root.childNodes[1].childNodes[1].addEventListener('click', appexportproc);
    this._tenantcombo.addEventListener('click', function(e) {
        this.showtenantmenu(true);
        e.stopImmediatePropagation();
    }.bind(this))
}
exportcon.prototype.showtenantmenu = function(show) {
    var getmenuposition = function() {
        var x = 0;
        var y = this._tenantcombo.offsetHeight + 4;
        var c = this._tenantcombo;
        while (c !== document.body) {
            x += c.offsetLeft;
            y += c.offsetTop;
            c = c.offsetParent;
        }
        return {x:x, y:y};
    }.bind(this);
    var globalclickhandler = function(e) {
        this.showtenantmenu(false);
    }.bind(this);
    var selecttenanthandler = function(e) {
        e.stopImmediatePropagation();
        this.showtenantmenu(false);
        var tenantid = e.currentTarget.getAttribute('tenant');
        for (var i = 0; i < this._data.tenantdata.length; i++) {
            var tenant = this._data.tenantdata[i];
            if (tenant.tenantid == tenantid) {
                this.currenttenant(tenant);
                return;
            }
        }
        this.currenttenant(null);
    }.bind(this);
    if (show) {
        var menu = this._tenantmenu;
        if (!menu) {
            this._tenantmenu = document.createElement('div');
            this._tenantmenu.setAttribute('class', 'combomenu');
            menu = this._tenantmenu;
            document.body.appendChild(menu);
            var tenanthtml = '';
            for (var i = 0; i < this._data.tenantdata.length; i++) {
                var tenant = this._data.tenantdata[i];
                tenanthtml += '<div tenant="' + tenant.tenantid + '">' + tenant.tenantname + '</div>';
            }
            menu.innerHTML = tenanthtml;
            for (var i = 0; i < menu.children.length; i++) {
                menu.children[i].onclick = selecttenanthandler;
            }
        }
        var pos = getmenuposition();
        menu.style.left = pos.x + 'px';
        menu.style.top = pos.y + 'px';
        menu.style.display = 'block';
        document.body.addEventListener('click', globalclickhandler);
    } else {
        var menu = this._tenantmenu;
        if (menu) menu.style.display = '';
        document.body.removeEventListener('click', globalclickhandler);
    }
}
exportcon.prototype.currenttenant = function(value) {
    if (value !== undefined) {
        this._currenttenant = value;
        this._tenantcombo.innerHTML = value ? value.tenantname : '';
    } else {
        return this._currenttenant;
    }
}
exportcon.prototype.showpage = function(page, total) {
    this._form.showpage(page, total);
}
exportcon.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this._tenantmenu = null;
        this.layout();
    } else {
        return this._data;
    }
}
exportcon.prototype.selected = function(value) {
    if (value !== undefined) {
        this._selected = value;
        if (value) {
            window.addclass(this._root, "selected");
        } else {
            window.removeclass(this._root, "selected");
        }
    } else {
        return this._selected || false;
    }
}
exportcon.prototype.layout = function () {
    this.currenttenant(this._data.tenantdata[0]);
    this._btngroup.data(this._data.btndata);
};
exportcon.prototype.showtable = function(value) {
    this._form.data(value);
}
// 显示左侧表格数据
exportcon.prototype.showoriginaldata = function(value) {
    this._form.originaldata(value);
}
exportcon.prototype.updateoriginalstatus = function(datas) {
    this._form.updateoriginalstatus(datas);
}
exportcon.prototype.setoriginalstatus = function(data, exported) {
    this._form.setoriginalstatus(data, exported);
}
// 将点击的数据加到右侧linked列表里
exportcon.prototype.tolinked = function(value) {
    this._form.adddata(value);
}
exportcon.prototype.exportdatas = function() {
    return this._form.selecteddatas();
}
