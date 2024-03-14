/**
 * @externs
 */

/**
 * @constructor
 */
var CKEDITOR = function () {

    /**
     * @type {Element}
     */
    this.container = null;

};

CKEDITOR.prototype = {
  "resize": function (width, height) {},
  "on": function (String, listener) {},
  "setData": function (data) {},
  "getData": function () {},
  "destroy": function () {},
};

CKEDITOR.replace = function (div, options) {};

/**
 * @constructor
 */
var CKEvent = function () {

    /**
     * @type {CKEDITOR}
     */
    this.editor = null;

};

