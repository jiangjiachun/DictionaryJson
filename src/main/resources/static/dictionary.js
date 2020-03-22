var dic_groups = [{"id":"分组_01","name":"第1组"},{"id":"分组_02","name":"第2组"},{"id":"分组_03","name":"第3组"},{"id":"分组_04","name":"第4组"},{"id":"分组_05","name":"第5组"},{"id":"分组_06","name":"第6组"},{"id":"分组_07","name":"第7组"},{"id":"分组_08","name":"第8组"},{"id":"分组_09","name":"第9组"},{"id":"分组_10","name":"第10组"},{"id":"分组_11","name":"第11组"},{"id":"分组_12","name":"第12组"}];
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