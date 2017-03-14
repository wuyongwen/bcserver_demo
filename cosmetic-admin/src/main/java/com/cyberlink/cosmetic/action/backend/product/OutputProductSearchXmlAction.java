package com.cyberlink.cosmetic.action.backend.product;

import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandNameAlias;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchKeyword;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;

@UrlBinding("/product/OutputProductSearchXml.action")
public class OutputProductSearchXmlAction extends AbstractAction{
	
	@SpringBean("product.BrandDao")
	private BrandDao brandDao;
	
	@SpringBean("product.ProductTypeDao")
	private ProductTypeDao productTypeDao;
	
	@SpringBean("product.StoreDao")
	private StoreDao storeDao ;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	@DefaultHandler
	public Resolution route() {
		String outputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><beauty_circle>" ;
		/**put your data here to output xml detail data**/
		Set<String> localeList = localeDao.getAvailableLocaleByType(LocaleType.PRODUCT_LOCALE);
		for( String curLocale : localeList ){			
			outputXml += "<store locale=\"" + xmlFormatValidation(curLocale) + "\">" ;
			List<Brand> curBrandList = brandDao.listAllBrandByLocale(curLocale);
			for( Brand curBrand : curBrandList ){
				outputXml += "<brand name=\"" + xmlFormatValidation(curBrand.getBrandName()) + "\" " ;
				outputXml += "index=\"" + xmlFormatValidation(curBrand.getBrandIndex().getIndex()) + "\" " ;
				if( curBrand.getBrandNameAliasList().size() >0 ){
					outputXml += "aka=\"" ;
					int aliasCount = 0 ;
					for(BrandNameAlias alias : curBrand.getBrandNameAliasList()){
						outputXml += xmlFormatValidation(alias.getAliasName()) ;
						aliasCount++;
						if( aliasCount < curBrand.getBrandNameAliasList().size() ){
							outputXml += "," ;
						}
					}
					outputXml += "\" " ;
				}
				outputXml += ">" ;
				int keywordCount = 0;
				for(ProductSearchKeyword keyword : curBrand.getKeywordList()){
					outputXml += xmlFormatValidation(keyword.getKeyword());
					keywordCount++;
					if( keywordCount < curBrand.getKeywordList().size() ){
						outputXml += "," ;
					}
				}
				outputXml += "</brand>" ;
			}
			
			List<ProductType> curTypeList = productTypeDao.listAllProdTypeByLocale(curLocale);
			for( ProductType curType : curTypeList ){
				outputXml += "<category name=\"" + xmlFormatValidation(curType.getTypeName()) + "\">"; 
				int keywordCount = 0;
				for( ProductSearchKeyword keyword : curType.getKeywordList() ){
					outputXml +=  xmlFormatValidation(keyword.getKeyword());
					keywordCount++;
					if( keywordCount < curType.getKeywordList().size() ){
						outputXml += "," ;
					}
				}
				outputXml += "</category>" ;
			}
			
			outputXml += "</store>";
		}
		/**end of xml output data**/
		outputXml += "</beauty_circle>" ;
		return new StreamingResolution("application/xml", outputXml);

	}
	
	public String xmlFormatValidation(String curString){
		String replacedString = curString.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;");
		//System.out.println(replacedString);
		return replacedString;
		
	}

}
