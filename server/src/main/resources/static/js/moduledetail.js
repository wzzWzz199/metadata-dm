/**
 * Created by huangjie on 2020-06-05
 */

// 模块详情
window.moduledetail = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'moduledetail');
    this._container.appendChild(this._root);
    // this._root = document.getElementById('moduledetail');
}
moduledetail.prototype.data = function(value) {
    if (value !== undefined) {
        this._data = value;
        this.layout();
    } else {
        return this._data;
    }
}
moduledetail.prototype.layout = function() {
    var returnclick = function(e) { // 返回
        if (this._options && this._options.returnproc) {
            this._options.returnproc(this._data);
        }
        e.stopImmediatePropagation(); // 阻止冒泡，阻止事件往下传递，如果在模块详情里的window下添加事件（拖拽），点击返回的时候就会触发拖拽，在事件完成后停止，就不会往下传递了
    }.bind(this); // 在方法里面用this,就要在方法外面bind(this)

    var upgradeclick = function(e) { // 升级
        if (this._status == 2) {
            this._options.upgradeproc(this._data, {ver:this._data.moduledata.currentver});
        } else if (this._options && this._options.upgradeproc) {
            if (this._upgradeformprod == true) {
                this._options.upgradeproc(this._data, this._packlist.current(), this._prodlist.current()); // this._packlist.current() 不传值就是获取this._current属性，代表当前对象
            } else {
                this._options.upgradeproc(this._data, this._packlist.current());
            }
        }
        e.stopImmediatePropagation();
    }.bind(this);

    this._root.innerHTML = "";
    var moduledata = this._data.moduledata;
    var logs = this._data.moduledata.logs;
    var progress = 0;
    if (logs.length > 0) {
        for (var i = 0; i < logs.length; i++) {
            progress = logs[logs.length - 1].progress;
        }
    }

    var content = [
        '<div class="container clearfix">',
            '<div class="libcontent clearfix">',
                '<span class="code">' + moduledata.code + '</span>',
                '<span class="info">' + moduledata.name + '</span>',
            '</div>',
            '<div class="versioninfo">',
                '<div class="clearfix">',
                    '<span>版本号：</span>',
                    '<span>' + moduledata.currentver != null ? moduledata.currentver : '' + '</span>',
                '</div>',
                '<span>更新日期：' + new Date(moduledata.datetime).format('yyyy-MM-dd hh:mm') + '</span>',
                // '<span>进度：</span>',
            '</div>',
            '<div class="btn">升级</div>',
            '<div class="backbtn title="返回"><span class="iconfont icon-houtui"></span></div>',
            '<div class="progresswrap clearfix">',
                // '<div class="endtime">' +
                //     '<span>预计完成时间：</span>' +
                //     '<span></span>' +
                // '</div>',
                '<div class="progressvalue"></div>',
                '<div class="progress">' +
                    '<div class="progressbar"></div>',
                '</div>',
            '</div>',
        '</div>',
        '<div class="upgradelog">',
            '<div class="container clearfix">',
                '<div class="packwrap">',
                    '<div class="packagelist"></div>',
                    '<div class="prodwrap">',
                        '<div class="checkwrap">',
                            '<span class="checkbtn"></span>',
                            '<span>从产品版本升级</span>',
                        '</div>',
                        '<div class="prodlist"></div>',
                    '</div>',
                '</div>',
                // '<div class="packagelist"></div>',
                '<div class="directory"></div>',
            '</div>',
            '<div class="log"></div>',
            '<div class="loading"><div><i></i><span>加载中...</span></div></div>',
        '</div>'
    ].join("");

    this._root.innerHTML = content;
    var checkwrap = this._root.childNodes[1].childNodes[0].childNodes[0].childNodes[1].childNodes[0];
    var prodwrap = this._root.childNodes[1].childNodes[0].childNodes[0].childNodes[1];
    this.upgradefromprod(false);

    checkwrap.addEventListener('click', function() {
       this.upgradefromprod(!this.upgradefromprod());
    }.bind(this));

    var data = this._data.packdata;
    var prodpackdata = this._data.prodpackdata; // 产品的包和补丁
    if (data === undefined) {
        data = [];
        prodpackdata = [];
        this.showloading(true);
    } else {
        this.showloading(false);
    }

    // 创建补丁
    this._patchlist = new patchlist(this._root.childNodes[1].childNodes[0].childNodes[1]);

    // 创建包-版本（产品）
    if (prodpackdata != null && prodpackdata.length) {  // 先判断 != null 再判断 length,不然会报 undefined 错误
        this._prodlist = new packagelist(this._root.childNodes[1].childNodes[0].childNodes[0].childNodes[1].childNodes[1], {clickproc: function(data) {  // 点击包，是在自己内部处理
            this._patchlist.data(data);
        }.bind(this)});
        this._prodlist.data(prodpackdata);
        removeclass(prodwrap, 'hideprodwrap');
    } else {
        addclass(prodwrap, 'hideprodwrap');
    }

    // 创建包-版本(项目)
    this._packlist = new packagelist(this._root.childNodes[1].childNodes[0].childNodes[0].childNodes[0], {clickproc: function(data) {  // 点击包，是在自己内部处理
            this._patchlist.data(data);
        }.bind(this)});
    this._packlist.data(data);

    this._root.childNodes[0].childNodes[3].addEventListener("click", returnclick); // 点击返回按钮，要把这个回调传出去，用于控制模块和模块详情的显示和隐藏,所以要写一个回调函数
    this._root.childNodes[0].childNodes[2].addEventListener("click", upgradeclick); // 同理

    // 创建日志
    this._log = new log(this._root.childNodes[1].childNodes[1]);
    this._progresscontainer = this._root.childNodes[0].childNodes[4].childNodes[1].childNodes[0];
    this._progressvalue = this._root.childNodes[0].childNodes[4].childNodes[0];
    this.status(moduledata.status);
    this._log.data(moduledata.logs);
}
moduledetail.prototype.showloading = function(loading) {
    if (loading) {
        addclass(this._root, 'loading');
    } else {
        removeclass(this._root, 'loading');
    }
}
moduledetail.STATUS_MAP = {
    0:'notupgraded',   // 可升级
    1:'upgradding', // 升级中
    2:'upgraderror',  // 升级异常
    3:'upgraded'  // 升级完成
}
moduledetail.prototype.upgradefromprod = function(value) {
    if (value !== undefined) {
        this._upgradeformprod = value;
        var packwrap = this._root.childNodes[1].childNodes[0].childNodes[0];
        if (value) {
            addclass(packwrap, 'showprod');
        } else {
            removeclass(packwrap, 'showprod');
        }
    } else {
        return this._upgradeformprod || false;
    }
}
moduledetail.prototype.status = function(value) {
    if (value !== undefined) {
        var statuschanged = function() {
            if (this._options && this._options.statusproc) {
                this._options.statusproc(this._status, this._data);
            }
        }.bind(this);

        if (this._status == value) return;
        if (this._status != null) {
            window.removeclass(this._root, moduledetail.STATUS_MAP[this._status]);
        }
        this._status = value;
        if (value != null) {
            if (!this._data) return;
            window.addclass(this._root, moduledetail.STATUS_MAP[value]);
            if (value == 1) {
                this._log.clearlogs();
            }
            if (value == 1 || value == 2) {
                window.addclass(this._root, 'showlog');
            } else {
                window.removeclass(this._root, 'showlog');
            }
        }
        statuschanged();
    } else {
        return this._status;
    }
}
moduledetail.prototype.progress = function(value) {
    if (value !== undefined) {
        this._progress = value;
        if (value > 100) value = 100;
        if (value < 0) value = 0;
        if (value == 100) {
            this.status(3);
        } else {
            // this._progresscontainer.innerHTML = + (value || 0) + '%';
            this._progresscontainer.style.width = + (parseFloat(value).toFixed(1) || 0) + '%';
            this._progressvalue.innerHTML = + (parseFloat(value).toFixed(1) || 0) + '%';
        }
    } else {
        return this._progress;
    }
}
moduledetail.prototype.addlogs = function(logs) {
    if (!logs || !logs.length) return;
    if (this._status != 1 && this._status != 2) return;
    var data = this._data.moduledata;
    for (var i = 0; i < logs.length; i++) {
        var log = logs[i];
        if (log.code == data.code && log.logs.length) {
            this._log.addlogs(log.logs);
            this.progress(log.logs[log.logs.length - 1].progress);
            break;
        }
    }
    // var moduletime = data.datetime;
    // var lastlog = log.logs[log.logs.length - 1];
    // var logtime = lastlog.datetime;
    // var progress = parseFloat(lastlog.progress) / 100;
    // var complationtime = (logtime - moduletime) / progress + moduletime;
    // this._complationtimecontainer.innerHTML = new Date(complationtime).format('yyyy-MM-dd hh:mm:ss');
}
// moduledetail.prototype.complationtime = function(logs) {
//     var data = this._data.moduledata;
//     var moduletime = data.datetime;
//     for (var i = 0; i < logs.length; i++) {
//         var log = logs[i];
//         if (log.code == data.code && log.logs.length) {
//             var logtime = log.logs[log.logs.length - 1].datetime;
//             var progress = log.logs[log.logs.length - 1].progress;
//             var complationtime = (logtime - moduletime) / progress;
//             this._complationtimecontainer.innerHTML = new Date(complationtime).format('MM-dd hh:mm:ss');
//             break;
//         }
//     }
// }
