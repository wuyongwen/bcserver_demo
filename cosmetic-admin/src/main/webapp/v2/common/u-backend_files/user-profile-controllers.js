var userProfileControllers = angular.module('userProfileControllers', []);

userProfileControllers.controller('userProfileCtrl', ['$scope', '$http', '$modal',
	function ($scope, $http, $modal) {
		
		$scope.openEditDisplayNameModal = function () {
			var modalInstance = $modal.open({
					templateUrl: 'EditDisplayNameModal.html',
					controller: 'EditDisplayNameModalCtrl',
					resolve: {
						userProfile: function () { 
							return $scope.userProfile;
						}
					}
				});
			
			modalInstance.result.then(function (result) {
					if (result.displayName.length > 0) {
						$http.get('user-profile.action?updateDisplayName', {
							params : {
								'displayName': result.displayName,
							}})
							.success(function (data) {
								init();
							});
					}
				}, function () {
					return;
				});
		};
	
		$scope.openEditDescriptionModal = function () {
			var modalInstance = $modal.open({
					templateUrl: 'EditDescriptionModal.html',
					controller: 'EditDescriptionModalCtrl',
					resolve: {
						userProfile: function () { 
							return $scope.userProfile;
						}
					}
				});
			
			modalInstance.result.then(function (result) {
				$http.get('user-profile.action?updateDescription', {
					params : {
						'description': result.description,
					}})
					.success(function (data) {
						init();
					});
				}, function () {
					return;
				});
		};
		
		$scope.openUploadAvatarModal = function () {
			var modalInstance = $modal.open({
					templateUrl: 'UploadAvatarModal.html',
					controller: 'UploadAvatarModalCtrl',
					backdrop: 'static'
				});
			
			modalInstance.result.then(function (result) {
					init();
				}, function () {
					return;
				});
		};
		
		$scope.openChangePasswordModal = function () {
			var modalInstance = $modal.open({
					templateUrl: 'ChangePasswordModal.html',
					controller: 'ChangePasswordModalCtrl'
				});
			
			modalInstance.result.then(function (result) {
				$http.get('user-profile.action?changePassword', {
					params : {
						'currentPassword': result.currentPassword,
						'newPassword': result.newPassword
					}})
					.success(function (data) {
						init();
					})
					.error(function (data) {
						alert(data.errorMessage);
					});
				}, function () {
					return;
				});
		};
		
		var init = function() {
			$http.get('user-profile.action?init')
				.success(function (data) {
					$scope.userProfile = data.userProfile;
				});
		};
		
		init();
	}]);

userProfileControllers.controller('EditDisplayNameModalCtrl',
	function ($scope, $modalInstance, userProfile) {
		
		$scope.userProfile = userProfile;
		$scope.displayName = '';
		
		$scope.isConfirmDisabled = function () {
			return $scope.displayName.length == 0;
		};
		
		$scope.confirm = function () {
			$modalInstance.close({
				'displayName': $scope.displayName
			});
		};
		
		$scope.cancel = function () {
			$modalInstance.dismiss();
		};
	});

userProfileControllers.controller('EditDescriptionModalCtrl',
	function ($scope, $modalInstance, userProfile) {
		
		$scope.userProfile = userProfile;
		$scope.description = userProfile.description;
		
		$scope.isConfirmDisabled = function () {
			return $scope.description.length == 0;
		};
		
		$scope.confirm = function () {
			$modalInstance.close({
				'description': $scope.description
			});
		};
		
		$scope.cancel = function () {
			$modalInstance.dismiss();
		};
	});

userProfileControllers.controller('UploadAvatarModalCtrl',
	function ($scope, $modalInstance, FileUploader) {
		
		$scope.uploader = new FileUploader({
	        url: 'user-profile.action?uploadAvatar',
	        autoUpload: true,
	        onCompleteAll: function() {
	        	$modalInstance.close();
			}
	    });
		
		$scope.uploader.filters.push({
		    name: 'imageFileFilter',
		    fn: function(item) {
		    	var isImageFile = item.type == 'image/jpeg'
		    		|| item.type == 'image/png';
		    	
		    	return isImageFile;
		    }
		});
		
		$scope.isFileChooserDisabled = function () {
			return $scope.uploader.isUploading;
		};
		
		$scope.isUploadAvatarStatusShow = function () {
			return $scope.uploader.isUploading;
		};
		
		$scope.isCancelDisabled = function () {
			return $scope.uploader.isUploading;
		};
		
		$scope.cancel = function () {
			$modalInstance.dismiss();
		};
	});

userProfileControllers.controller('ChangePasswordModalCtrl',
	function ($scope, $modalInstance) {
		
		$scope.currentPassword = '';
		$scope.newPassword = '';
		$scope.newPasswordVerified = '';
		
		$scope.isConfirmDisabled = function () {
			if ($scope.currentPassword.length == 0
				|| $scope.newPassword.length == 0
				|| $scope.newPasswordVerified.length == 0) {
				return true;
			}
			
			if ($scope.currentPassword == $scope.newPassword) {
				return true;
			}
			
			return $scope.newPassword != $scope.newPasswordVerified;
		};
		
		$scope.confirm = function () {
			$modalInstance.close({
				'currentPassword': $scope.currentPassword,
				'newPassword': $scope.newPassword
			});
		};
		
		$scope.cancel = function () {
			$modalInstance.dismiss();
		};
	});
