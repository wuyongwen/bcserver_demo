package com.cyberlink.cosmetic.modules.common.dao.hibernate;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.model.Locale;

public class LocaleDaoHibernate extends AbstractDaoCosmetic<Locale, Long>
    implements LocaleDao {

    private String regionOfFindlocale = "com.cyberlink.cosmetic.modules.common.model.Locale.query.getAvailableInputLocale";
    private String regionOfFindDefaultLocale = "com.cyberlink.cosmetic.modules.common.model.Locale.query.getDefaultInputLocale";
    private String regionOfGetAvailableLocaleByType = "com.cyberlink.cosmetic.modules.common.model.Locale.query.getAvailableLocaleByType";
    
    @Override
    public Locale getAvailableInputLocale(String inputLocale) {
        if(inputLocale == null)
            return null;
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("inputLocale", inputLocale));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        Locale result = uniqueResult(d, regionOfFindlocale);
        if(result == null)
            result = getDefaultLocale();

        return result;
    }

    @Override
    public Set<String> getLocaleByType(String inputLocale, LocaleType localeType) {
        Set<String> resultList = new LinkedHashSet<String>(0);
        if(inputLocale == null || localeType == null)
            return resultList;
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("inputLocale", inputLocale));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        Locale locale = uniqueResult(d);
        if(locale == null)
            locale = getDefaultLocale();
        
        switch(localeType) {
            case USER_LOCALE:
                resultList = locale.getUserLocaleList();
                break;
            case POST_LOCALE:
                resultList.add(locale.getPostLocale());
                break;
            case PRODUCT_LOCALE:
                resultList.add(locale.getProductLocale());
                break;
            default:
                break;
        }
        return resultList;
    }

    @Override
    public List<String> getAvailableInputLocaleByType(String value, LocaleType localeType) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq(localeType.getVariableName(), value));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.property("inputLocale"));
        return findByCriteria(d);
    }
    
    @Override
    public Set<String> getAvailableLocaleByType(LocaleType localeType) {
        Set<String> resultList = new LinkedHashSet<String>(0);
        if(localeType == null)
            return resultList;
        
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        d.setProjection(Projections.projectionList().add(Projections.groupProperty(localeType.getVariableName())));
        List<String> mixList = findByCriteria(d, regionOfGetAvailableLocaleByType);
        if(mixList == null)
            return resultList;
        for(String mix : mixList) {
            resultList.addAll(Arrays.asList(mix.split(Locale.localeDelimiter)));
        }
        
        Locale rowLocale = getDefaultLocale();
        resultList.add(localeType.getValue(rowLocale));
        return resultList;
    }

    @Override
    public Locale getDefaultLocale() {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("inputLocale", LocaleType.getDefaultSourceLocale()));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(d, regionOfFindDefaultLocale);
    }
    
    @Override
    public Locale findByLocale(String inputLocale){
    	DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("inputLocale", inputLocale));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return uniqueResult(d);
    }
}
