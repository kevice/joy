<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/joy/commons/include/inc-all.jsp"%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="关系数据库对象管理页面">
<meta name="author" content="Kevice">

<title>关系数据库对象管理页面</title>

<script type="text/javascript">
	function showDetail(id) {
		$.layer({
		    type : 2,
		    title : '关系数据库对象详情',
		    iframe : {src : '${ctx}/mdRdbObj/get?id='+id},
		    area : ['750px' , '466px'],
		    offset : ['50px','']
		});
	}
</script>

</head>

<body>

	<form action="${ctx}/mdRdbObj/list" method="POST">
		<div class="panel panel-default">
			<div class="panel-heading">
				<tags:pageNavTitle />
			</div>
			<div class="panel-body">
				<div class="joy-search-bar">
					<div class="form-horizontal" role="form" style="height: 36px">
						<div class="form-group">
							<label for="dsId" class="col-sm-1 control-label">数据源</label>
							<div class="col-sm-2">
								<select class="form-control" name="dsId" value="${dsId}"> 
									<c:forEach items="${joy:getAllDataSrc()}" var="entry">
										<option value="${entry.id}" class="joy-select-option">${entry.name}</option>
									</c:forEach>
								</select>
							</div>
							<label for="name" class="col-sm-1 control-label">对象名</label>
							<div class="col-sm-2">
								<input class="form-control" value="${name}"
									name="name" placeholder="请输入对象名" data-joy-props="operator:'ilike'">
							</div>
							<label for="comment" class="col-sm-1 control-label">对象注释</label>
							<div class="col-sm-2">
								<input class="form-control" value="${comment}"
									name="comment" placeholder="请输入对象注释" data-joy-props="operator:'ilike'">
							</div>
							<div class="col-sm-3">
								<button id="submitBtn" class="btn btn-default">
									<i class="icon-search"></i>&nbsp;查询
								</button>
							</div>
						</div>
					</div>
				</div>

				<div class="table-responsive">
					<table
						class="table table-condensed table-hover table-striped table-bordered joy-table">
						<thead>
							<tr>
								<th class="joy-table-seq-col" width="30px">#</th>
<!-- 								<th width="70px">操作</th> -->
								<th><tags:orderColumn property="name" columnName="对象名" defaultOrder="ASC" /></th>
								<th>对象注释</th>
								<th>对象类型</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${pageStore.result}" var="p" varStatus="stauts">
								<tr>
									<td class="joy-table-seq-col">${stauts.index+1}</td>
<%-- 									<td><tags:listOperations id="${p.dsId}-${p.name}"/></td> --%>
									<td>${p.name}</td>
									<td>${p.comment}</td>
									<td>${p.type}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>

				<tags:pagination />

			</div>
		</div>
	</form>

</body>
</html>