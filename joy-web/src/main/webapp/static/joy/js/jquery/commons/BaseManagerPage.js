define(['joy', 'layer', 'css!3rd/jquery/layer/skin/layer'], function(joy, layer) {
	
	return joy.createClass({

        queryBtn : null,
        toggleSeniorQueryBtn : null,
        seniorQueryBtn : null,
        resetSeniorQueryBtn : null,
        addNewBtn : null,
        deleteBtn : null,
        queryConditionForm : null,
        seniorQueryConditionPane : null,
        seniorQueryConditionForm : null,
        resultGrid : null,
        listPage : null,
        editPage : null,
        detailPage : null,

        init : function() {
            this.queryBtn = $("#queryBtn");
//            this.toggleSeniorQueryBtn =  $("#toggleSeniorQueryBtn");
//            this.seniorQueryBtn =  $("#seniorQueryBtn");
//            this.resetSeniorQueryBtn =  $("#resetSeniorQueryBtn");
//            this.addNewBtn =  $("#addNewBtn");
//            this.deleteBtn =  $("#deleteBtn");
//            this.queryConditionForm =  $("#queryConditionForm");
//            this.seniorQueryConditionPane =  $("#seniorQueryConditionPane");
//            this.seniorQueryConditionForm =  $("#seniorQueryConditionForm");
//            if(this.seniorQueryConditionForm && this.resultGrid) {
//                var ds = this.resultGrid.getDataStore();
//                this.seniorQueryConditionForm.set("store", ds ? ds.dataStoreName : "");
//            }
//            this.resultGrid =  $("#resultGrid");
//            this.listPage =  $("#listPage");
//            this.editPage =  $("#editPage");
//            this.detailPage =  $("#detailPage");
//            if(this.resultGrid) {
//                this.resultGrid.controler = this;
//            }

            this.bindEvent();
        },

        bindEvent : function() {
            this.queryBtn.bind('click', this.queryConditionForm, this.query);
//            this.seniorQueryBtn.bind('click', this.seniorQueryConditionForm, this.query);
//            this.resetSeniorQueryBtn.bind('click', this.resetCondition);
//            this.toggleSeniorQueryBtn.bind('click', this.toggleSeniorQuery);
//            this.addNewBtn.bind('click', this.showEditPage);
//            this.deleteBtn.bind('click', this.deleteRows);
            //this.editPage.bind('click', this.createEditPage);
            //this.detailPage.bind('click', this.createDetailPage);
        },

        /**
         * 执行查询
         */
        query : function(event) {
            var queryForm = event.data;
            // 校验表单
            if (queryForm.validateForm()) {
                if(queryForm === this.seniorQueryConditionForm) {
                    this.toggleSeniorQuery();
                }
                // 获取查询条件
                var condition = queryForm.getQueryCondition();
                // 设置查询条件
                this.resultGrid.setQueryCondition(condition);
                // 重新加载表格数据
                this.resultGrid.load();
            }
        }



	});

});