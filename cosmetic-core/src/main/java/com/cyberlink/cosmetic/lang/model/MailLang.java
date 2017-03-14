package com.cyberlink.cosmetic.lang.model;

public class MailLang extends AbstractLang {

	public MailLang(String locale) {
		super(locale);
	}

	public String getCopyRight() {
		try {
			return resBundle.getString("email.copyRight");
		} catch (Exception e) {
			return "";
		}
	}

	// Subject
	public String getSubjectSuspension() {
		try {
			return resBundle.getString("email.subject.suspension");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSubjectImpersonationSuspension() {
		try {
			return resBundle
					.getString("email.subject.impersonation.suspension");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSubjectImpersonationInvestigation() {
		try {
			return resBundle
					.getString("email.subject.impersonation.investigation");
		} catch (Exception e) {
			return "";
		}
	}

	// Free Sample
	public String getFreeSampleSubject() {
		try {
			return resBundle.getString("email.freeSample.subject");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleHello() {
		try {
			return resBundle.getString("email.freeSample.hello");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleDescriptionStore(String title) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.description.store"),
					title);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleDescriptionHome(String title) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.description.home"),
					title);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleYourInfo() {
		try {
			return resBundle.getString("email.freeSample.yourInfo");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleUserName(String userName) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.userName"), userName);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSamplePhone(String phone) {
		try {
			return String.format(resBundle.getString("email.freeSample.phone"),
					phone);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleEmail(String email) {
		try {
			return String.format(resBundle.getString("email.freeSample.email"),
					email);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleReceiveStore(String storeLocation,
			String storeName) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.receiveStore"),
					storeLocation, storeName);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleUserAddress(String userAddress) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.userAddress"),
					userAddress);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleProdName(String prodName) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.prodName"), prodName);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleThankForJoin() {
		try {
			return resBundle.getString("email.freeSample.thankForJoin");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleEnd() {
		try {
			return resBundle.getString("email.freeSample.end");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleAnyProblemStore() {
		try {
			return resBundle.getString("email.freeSample.anyProblem.store");
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleAnyProblemHome() {
		try {
			return resBundle.getString("email.freeSample.anyProblem.home");
		} catch (Exception e) {
			return "";
		}
	}
	
	// Free Sample Coupon
	public String getFreeSampleCouponDescription(String title) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.coupon.description"),
					title);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCouponGetcode() {
		try {
			return resBundle.getString("email.freeSample.coupon.getcode");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCouponEnd() {
		try {
			return resBundle.getString("email.freeSample.coupon.end");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCouponAnyProblem() {
		try {
			return resBundle.getString("email.freeSample.coupon.anyProblem");
		} catch (Exception e) {
			return "";
		}
	}

	// Consultation
	public String getConsultationSubject(String tittle) {
		try {
			return String.format(
					resBundle.getString("email.consultation.subject"), tittle);
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationHello(String userName) {
		try {
			return String.format(
					resBundle.getString("email.consultation.hello"), userName);
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationDescription(String endTime, String description) {
		try {
			return String.format(
					resBundle.getString("email.consultation.description"),
					endTime, description);
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationYourInfo() {
		return getFreeSampleYourInfo();
	}

	public String getConsultationUserName(String userName) {
		return getFreeSampleUserName(userName);
	}

	public String getConsultationBirhday(String birhday) {
		try {
			return String.format(
					resBundle.getString("email.consultation.birhday"), birhday);
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationPhone(String phone) {
		return getFreeSamplePhone(phone);
	}

	public String getConsultationEmail(String email) {
		return getFreeSampleEmail(email);
	}

	public String getConsultationReceiveStore(String storeLocation,
			String storeName) {
		try {
			return String.format(
					resBundle.getString("email.consultation.receiveStore"),
					storeLocation, storeName);
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationEnd() {
		try {
			return resBundle.getString("email.consultation.end");
		} catch (Exception e) {
			return "";
		}
	}

	public String getConsultationProblem() {
		return getFreeSampleAnyProblemStore();
	}
	
	// Free Sample Customer
	public String getFreeSampleCustomerSubject() {
		try {
			return resBundle.getString("email.freeSample.customer.subject");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCustomerContent1(String title) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.customer.content1"),
					title);
		} catch (Exception e) {
			return "";
		}
	}

	public String getFreeSampleCustomerContent2(String dateSting) {
		try {
			return String.format(
					resBundle.getString("email.freeSample.customer.content2"),
					dateSting);
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCustomerContent3() {
		try {
			return resBundle.getString("email.freeSample.customer.content3");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getFreeSampleCustomerContent4() {
		try {
			return resBundle.getString("email.freeSample.customer.content4");
		} catch (Exception e) {
			return "";
		}
	}

	// Sign UP
	public String getSignUpSubject() {
		try {
			return resBundle.getString("email.signUp.subject");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpAnyProblem(String removeUrl) {
		try {
			return String.format(
					resBundle.getString("email.signUp.anyProblem"), removeUrl);
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpContent1(String name) {
		try {
			return String.format(resBundle.getString("email.signUp.content1"),
					name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpContent2() {
		try {
			return resBundle.getString("email.signUp.content2");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpContent3() {
		try {
			return resBundle.getString("email.signUp.content3");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpContent4() {
		try {
			return resBundle.getString("email.signUp.content4");
		} catch (Exception e) {
			return "";
		}
	}

	public String getSignUpContent5() {
		try {
			return resBundle.getString("email.signUp.content5");
		} catch (Exception e) {
			return "";
		}
	}

	// Forgot Password
	public String getForgotPasswordSubject() {
		try {
			return resBundle.getString("email.forgotPassword.subject");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getForgotPasswordPageName() {
		try {
			return resBundle.getString("email.forgotPassword.pageName");
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordAnyProblem() {
		try {
			return resBundle.getString("email.forgotPassword.anyProblem");
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordContent1(String name) {
		try {
			return String.format(resBundle.getString("email.forgotPassword.content1"),
					name);
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordContent2() {
		try {
			return resBundle.getString("email.forgotPassword.content2");
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordContent3() {
		try {
			return resBundle.getString("email.forgotPassword.content3");
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordContent4() {
		try {
			return resBundle.getString("email.forgotPassword.content4");
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getForgotPasswordContent5() {
		try {
			return resBundle.getString("email.forgotPassword.content5");
		} catch (Exception e) {
			return "";
		}
	}

	public String getForgotPasswordEnd() {
		try {
			return resBundle.getString("email.forgotPassword.end");
		} catch (Exception e) {
			return "";
		}
	}
}