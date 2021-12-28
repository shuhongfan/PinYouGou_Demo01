 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,
										   goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		// 商品介绍
		$scope.entity.goodsDesc.introduction=editor.html()
		serviceObject=goodsService.add( $scope.entity  ).success(
			function(response){
				alert(response.messgae);
				if(response.success){
					//重新查询
		        	$scope.entity={}
					// 清空富文本编辑器
					editor.html("")
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	// 图片实体
	$scope.image_entity = {};
	$scope.uploadImage=function() {
	    uploadService.upload().success(function(response) {
	        if (response.error==0){
				// 成功
				$scope.image_entity.url=response.url;
			}
	    })
	}

	// 商品实体
	$scope.entity={
		goodsDesc: {
			itemImages:[],
			specificationItems:[]
		}
	}
	// 添加到图片列表
	$scope.add_image_entity = function() {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	$scope.remove_image_entity = function(index) {
	    $scope.entity.goodsDesc.itemImages.splice(index,1)
	}

	$scope.itemCat1List=[]
	// 查询商品一级分类列表
	$scope.selectItemCat1List = function() {
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCat1List = response
		})
	}

	// 查询商品二级分类列表
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(function(response) {
		    $scope.itemCat2List=response
		})
	})

	// 查询商品三级分类列表
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat3List=response
		})
	})

	// 查询模板ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.goods.typeTemplateId=response.typeId
		})
	})

	// 查询品牌列表
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.typeTemplate = response

			// 品牌列表有字符串转换为对象
			$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds)

			// 扩展属性
			$scope.entity.goods.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)
		})

		typeTemplateService.findSpecList(newValue).success(function(response) {
		    $scope.specList = response
		})
	})

	// 更新选中的规格
	$scope.updateSpecItems = function($event,name,value) {
		// 在集合中查询规格名称为某值的对象
		var searchObjectByKey = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"name",name);
		if (searchObjectByKey!=null){
			// 有此规格
			if ($event.target.checked){
				// 选中
				searchObjectByKey.values.push(value)
			} else {
				// 如果是取消选中
				searchObjectByKey.values.splice(searchObjectByKey.values.indexOf(value),1)
				if (searchObjectByKey.values.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
						$scope.entity.goodsDesc.specificationItems.indexOf(searchObjectByKey),1
					)
				}
			}
		} else {
			// 无此规格
			$scope.entity.goodsDesc.specificationItems.push({
				name : name,
				values : [value]
			})
		}
	}

	$scope.createItemList=function() {
	    var items = $scope.entity.goodsDesc.specificationItems
		$scope.entity.itemList=[
			{   spec:{},
				price:0,
				num:99999,
				status:'1',
				isDefault:'0'
			}
		]
		for (let i = 0; i < items.length; i++) {
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].name,items[i].values)
		}
	}

	addColumn=function(list,name,values) {
		var newList = []
		for (var i = 0; i < list.length; i++) {
			let oldRow = list[i];
			for (var j = 0; j < values.length; j++) {
				// 深克隆
				var newRow = JSON.parse(JSON.stringify(oldRow))
				newRow.spec[name]=values[j]
				newList.push(newRow)
			}
		}
		return newList
	}
});	
