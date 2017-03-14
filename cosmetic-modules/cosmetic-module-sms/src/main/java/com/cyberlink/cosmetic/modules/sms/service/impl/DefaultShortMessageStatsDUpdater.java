package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryEvent;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryVerifyEvent;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageStatsDUpdater;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;

public class DefaultShortMessageStatsDUpdater extends AbstractService implements ShortMessageStatsDUpdater {

    private static final String SMS_DELIVERY_SUCCEEDED_TEMPLATE = "sms.%s.%s.delivery.%s.succeeded";

    private static final String SMS_DELIVERY_SUCCESS_VERIFIED_TEMPLATE = "sms.%s.%s.delivery.%s.success.verified";

    private static final String SMS_DELIVERY_VERIFIED_ELAPSED_PER_REQUEST_TEMPLATE = "sms.%s.%s.delivery.%s.verified.elapsed.%s";

    private static final String SMS_DELIVERY_VERIFIED_ELAPSED_PER_USER_TEMPLATE = "sms.%s.delivery.verified.elapsed.%s";

    private StatsDUpdater statsDUpdater;

    public void setStatsDUpdater(StatsDUpdater statsDUpdater) {
        this.statsDUpdater = statsDUpdater;
    }

    private void increment(String aspect) {
        statsDUpdater.increment(aspect);
    }

    @Override
    public void recordDeliverySucceeded(ShortMessageDeliveryEvent event) {
        String metricName = toMetricName(SMS_DELIVERY_SUCCEEDED_TEMPLATE, event.getCountryCode(),
                event.getServiceProvider(), event.getIndex());
        increment(metricName);
    }

    private String toMetricName(String metricFormat, String countryCode, ShortMessageServiceProvider serviceProvider,
            int index) {
        return String.format(metricFormat, trimIllegal(countryCode), StringUtils.lowerCase(serviceProvider.name()),
                String.valueOf(index));
    }

    private String trimIllegal(String countryCode) {
        return StringUtils.replace(countryCode, "+", "");
    }

    @Override
    public void recordDeliverySuccessVerified(ShortMessageDeliveryVerifyEvent event) {
        increment(getVerifiedMetric(event));
        increment(getTimeElapsedMetricPerRequest(event));
        increment(getTimeElapsedMetricPerUser(event));
    }

    private String getVerifiedMetric(ShortMessageDeliveryVerifyEvent event) {
        return toMetricName(SMS_DELIVERY_SUCCESS_VERIFIED_TEMPLATE, trimIllegal(event.getCountryCode()),
                event.getServiceProvider(), event.getIndexOfSuccess());
    }

    private String getTimeElapsedMetricPerRequest(ShortMessageDeliveryVerifyEvent event) {
        return String.format(SMS_DELIVERY_VERIFIED_ELAPSED_PER_REQUEST_TEMPLATE, trimIllegal(event.getCountryCode()),
                StringUtils.lowerCase(event.getServiceProvider().name()), event.getIndexOfSuccess(),
                getGroupOfElapsed(event.getTimeOfSMSSent()));
    }

    private String getTimeElapsedMetricPerUser(ShortMessageDeliveryVerifyEvent event) {
        return String.format(SMS_DELIVERY_VERIFIED_ELAPSED_PER_USER_TEMPLATE, trimIllegal(event.getCountryCode()),
                getGroupOfElapsed(event.getTimeOfFirstSent()));
    }

    private String getGroupOfElapsed(Long timeOfSMSSent) {
        final Long elapsed = Calendar.getInstance().getTimeInMillis() - timeOfSMSSent;
        if (elapsed < 1000) {
            return "001";
        }
        if (elapsed < 2000) {
            return "002";
        }
        if (elapsed < 3000) {
            return "003";
        }
        if (elapsed < 4000) {
            return "004";
        }
        if (elapsed < 5000) {
            return "005";
        }
        if (elapsed < 6000) {
            return "006";
        }
        if (elapsed < 7000) {
            return "007";
        }
        if (elapsed < 8000) {
            return "008";
        }
        if (elapsed < 9000) {
            return "009";
        }
        if (elapsed < 10000) {
            return "010";
        }
        if (elapsed < 15000) {
            return "015";
        }
        if (elapsed < 20000) {
            return "020";
        }
        if (elapsed < 30000) {
            return "030";
        }
        if (elapsed < 40000) {
            return "040";
        }
        if (elapsed < 5000) {
            return "050";
        }
        if (elapsed < 60000) {
            return "060";
        }
        if (elapsed < 90000) {
            return "090";
        }
        if (elapsed < 120000) {
            return "120";
        }
        if (elapsed < 180000) {
            return "180";
        }
        if (elapsed < 240000) {
            return "240";
        }
        if (elapsed < 360000) {
            return "360";
        }
        if (elapsed < 480000) {
            return "480";
        }
        if (elapsed < 600000) {
            return "600";
        }
        return "999";
    }
}
