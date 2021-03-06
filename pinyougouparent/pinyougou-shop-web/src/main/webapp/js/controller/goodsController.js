 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,
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
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if (id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// 商品介绍
				editor.html($scope.entity.goodsDesc.introduction)

				// 商品图片
				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages)

				// 扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems)

				// 规格选择
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems)

				// 转换SKU列表中的规格对象
				for (var i = 0; i < $scope.entity.itemList.length; i++) {
					console.log($scope.entity.itemList[i])
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec)
				}
			}
		);
	}
	
	//保存 
	$scope.save=function(){
		//提取文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					alert('保存成功');
					$scope.entity={};
					editor.html("");
					location.href='goods.html'
				}else{
					alert(response.message);
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
			if ($location.search()['id']==null) {
				// 判断是否是增加商品
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)
            }

		})

		typeTemplateService.findSpecList(newValue).success(function(response) {
		    $scope.specList = response
		})
	})

	// 更新选中的规格
	$scope.updateSpecItems = function($event,name,value) {
		// 在集合中查询规格名称为某值的对象
		var searchObjectByKey = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		console.log(searchObjectByKey)
		if (searchObjectByKey!=null){
			// 有此规格
			if ($event.target.checked){
				// 选中
				searchObjectByKey.attributeValue.push(value)
			} else {
				// 如果是取消选中
				searchObjectByKey.attributeValue.splice(searchObjectByKey.attributeValue.indexOf(value),1)
				if (searchObjectByKey.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
						$scope.entity.goodsDesc.specificationItems.indexOf(searchObjectByKey),1
					)
				}
			}
		} else {
			// 无此规格
			$scope.entity.goodsDesc.specificationItems.push({
				attributeName : name,
				attributeValue : [value]
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
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
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

	$scope.status=['未审核','审核通过','审核未通过','已关闭']

	// 商品分类列表
	$scope.itemCatList = []
	// 查询商品分类列表
	$scope.findItemCatList = function() {
		itemCatService.findAll().success(function (response) {
			for (var i = 0; i < response.length; i++) {
				$scope.itemCatList[response[i].id] = response[i].name
			}
		})
    }

	// 判断规格与规格选项是否应该被勾选
	$scope.checkAttributeValue = function(specName,optionName) {
		var items = $scope.entity.goodsDesc.specificationItems;
		let object = $scope.searchObjectByKey(items,"attributeName",specName);
		if (object!=null){
			if (object.attributeValue.indexOf(optionName)>=0){
				// 如果能够查询规格选项
				return true
			} else {
				return false
			}
		} else {
			return false
		}
	}

	/**
	 * 上下架
	 * @param marketabel
	 */
	$scope.updateMarketabel=function (marketabel) {
		goodsService.updateMarketabel( $scope.selectIds , marketabel).success(function (response) {
			alert(response.message)	;
			if(response.success){
				$scope.reloadList();
				$scope.selectIds=[];
			}
		})
	}
});	
