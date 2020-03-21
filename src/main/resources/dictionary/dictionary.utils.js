/*
 *  Description: 前端处理json字典插件
 *  Author: jjc
 */
;(function($, undefined) {
	var pluginName = 'dicData';
	var defaults = {
		type : 'select',
		id : '',
		data : [],
		exclude : []
	// 排除字典id
	};

	function Plugin(element, options) {
		this.element = element;

		this._element = $(element);

		this.options = $.extend({}, defaults, options);

		this._defaults = defaults;

		this._name = pluginName;

		// 初始化方法
		this.init();
	}

	Plugin.prototype = {
		init : function() {
			var id = this._element.data('id');
			if (id) {
				this.options.id = id;
			}
			this.clear();
			if (this.options.type == 'select') {
				this.start();
			}
		},
		start : function() {
			var data = this.data();
			for (var i = 0; i < data.length; i++) {
				var dic = data[i];
				var option = $('<option>', {
					'value' : dic.id,
					'text' : dic.name
				});
				if (dic.id == this.options.id) {
					option.attr('selected', 'selected');
				}
				option.appendTo(this._element);
			}
		},
		clear : function() {
			this._element.find("option[value != '']").remove();
		},
		data : function() {
			var data = this.options.data;

			if (typeof data == 'function') {
				data = this.options.data();
			}

			if (!data || data.length == 0) {
				data = this._element.data('data');
			}
			if (!data) {
				data = this._defaults.data;
			}
			return this.exclude(data);
		},
		exclude : function(data) {
			var ex = this.options.exclude;
			if (ex && Array.isArray(ex) && ex.length <= 0) {
				return data;
			}
			var array = [];
			for (var i = 0; i < data.length; i++) {
				var dic = data[i];
				if (!ex.includes(dic.id)) {
					array.push(dic);
				}
			}
			return array;
		}
	}
	$.fn[pluginName] = function(options) {
		return this.each(function() {
			$.data(this, 'plugin_' + pluginName, new Plugin(this, options));
		});
	}
})(jQuery);

;(function($, undefined) {
	var pluginName = 'dicName';
	var defaults = {
		id : '',
		data : []
	};

	function Plugin(element, options) {
		this.element = element;
		this._element = $(element);

		this.options = $.extend({}, defaults, options);

		this._defaults = defaults;

		this._name = pluginName;

		// 初始化方法
		this.init();
	}

	Plugin.prototype = {
		init : function() {
			this.options.data = this.data();
			this.options.id = this.id();
			this.start();
		},
		start : function() {
			if (!this.options.data || !this.options.id) {
				return;
			}
			this.name(this.options.data, this.options.id);
		},
		data : function() {
			var data = this._element.data('data');
			if (data) {
				return eval(data);
			}
			return this.options.data;
		},
		id : function() {
			var id = this._element.data('id');
			if (id) {
				return id;
			}
			return this.options.id;
		},
		name : function(data, id) {
			for (var i = 0; i < data.length; i++) {
				var dic = data[i];
				if (dic.id == id) {
					this._element.text(dic.name);
					break;
				} else if (dic.nodes && dic.nodes.length > 0) {
					this.name.call(this, dic.nodes, id);
				}
			}
		}
	}
	$.fn[pluginName] = function(options) {
		this.each(function() {
			new Plugin(this, options);
		});
	}
})(jQuery);

jQuery.extend({
	dicName : function(id, data) {
		for (var i = 0; i < data.length; i++) {
			var dic = data[i];
			if (dic.id == id) {
				return dic.name;
			}
		}
	}
})