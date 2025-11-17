/**
 * Created by huangjie on 2020-07-06
 */
// 高级导出
window.advexport = function(container, options) {
    this._container = container;
    this._options = options;
    this._root = document.createElement('div');
    this._root.setAttribute('class', 'advcontent clearfix');
    this._container.append(this._root);

    this._root.innerHTML = '';
    var content = [
        // '<label>表:</label>',
        '<div class="textwrap"><div><input type="text" value="" placeholder="请输入表"></div></div>',
        // '<label>查询字段:</label>',
        '<div class="textwrap"><div><input type="text" value="" placeholder="请输入查询字段"></div></div>',
        // '<label>where条件:</label>',
        '<div class="textwrap"><div><input type="text" value="" placeholder="请输入where条件"></div></div>',
        '<div class="exportbtn">导出</div>'
    ].join('');
    this._root.innerHTML = content;

    var table = this._root.childNodes[0].childNodes[0].childNodes[0];
    var fields = this._root.childNodes[1].childNodes[0].childNodes[0];
    var where = this._root.childNodes[2].childNodes[0].childNodes[0];

    var advexportproc = function() {
        if (this._options && this._options.advexportproc) {
            this._options.advexportproc({table:table.value, fields:fields.value, where:where.value});
        }
    }.bind(this);
    this._root.childNodes[3].addEventListener('click', advexportproc);
}
