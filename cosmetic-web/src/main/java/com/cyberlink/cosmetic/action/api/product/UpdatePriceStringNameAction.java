package com.cyberlink.cosmetic.action.api.product;

import java.util.List;
import java.util.Locale;

import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.Store;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/UpdatePriceStringName.action")
public class UpdatePriceStringNameAction extends AbstractProductAction{

	@SpringBean("product.StoreDao")
	protected StoreDao storeDao ;
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao ;

	@SpringBean("product.StorePriceRangeDao")
	protected StorePriceRangeDao storePriceRangeDao;
	
	@DefaultHandler
    public Resolution route() {
		List<Store> onlineStoreList = storeDao.findAll() ;
		for( Store store : onlineStoreList ){
			List<Product> prodList = productDao.listProdByLocale(store.getLocale());
			for( Product prodItem : prodList ){
				setPriceString(prodItem);
				prodItem.setPriceRange(storePriceRangeDao
		    			.findPriceRangeByLocalePrice(prodItem.getLocale(), prodItem.getPrice()));
				productDao.update(prodItem);
			}
		}
		return success();
	}
	
	public void setPriceString( Product prodItem ){
		switch( prodItem.getLocale() ){
			case "de_DE":
				prodItem.setPriceString( "ab EUR " + String.format(Locale.GERMANY,"%,.2f", prodItem.getPrice() ) ) ;
				break;
			case "fr_FR":
				prodItem.setPriceString( "à partir de EUR " + String.format(Locale.GERMANY,"%,.2f", prodItem.getPrice() ) ) ;
				break;
			case "en_GB":
				prodItem.setPriceString( "from \u00A3" + String.format(Locale.UK,"%.2f", prodItem.getPrice() ) ) ;
				break;
			case "ja_JP":
				prodItem.setPriceString( "\u00A5 " + String.format("%,.0f", prodItem.getPrice() ) + "より" ) ;
				break;
			case "zh_CN":
				prodItem.setPriceString( "\u00A5" + String.format("%.0f", prodItem.getPrice() ) ) ;
				break;
			case "zh_TW":
				prodItem.setPriceString( "$" + String.format("%,.0f", prodItem.getPrice() ) ) ;
				break;
			case "en_CA":
				prodItem.setPriceString( "from CDN$ " + String.format("%.2f", prodItem.getPrice() ) ) ;
				break;
			case "en_US":
			default:
				prodItem.setPriceString( "from $" + String.format("%.2f", prodItem.getPrice() ) ) ;
				break;
		}
	}
	
}
