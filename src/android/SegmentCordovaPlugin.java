package com.segment.cordova.plugin;

import com.segment.analytics.Analytics;
import com.segment.analytics.Options;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.segment.analytics.android.integrations.appboy.AppboyIntegration;

public class SegmentCordovaPlugin extends CordovaPlugin {

    private static final String ACTION_WITH_CONFIGURATION = "startWithConfiguration";
    private static final String ACTION_IDENTIFY = "identify";
    private static final String ACTION_TRACK = "track";
    private static final String ACTION_SCREEN = "screen";
    private static final String ACTION_GROUP = "group";
    private static final String ACTION_ALIAS = "alias";
    private static final String ACTION_GET_ANONYMOUS_ID = "getAnonymousId";
    private static final String ACTION_RESET = "reset";

    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        int length = args.length();

        if (length > 0) {
            if (ACTION_WITH_CONFIGURATION.equals(action) && length > 1) {
                this.startWithConfiguration(args.getString(0), args.getJSONObject(1), callbackContext);
                return true;
            } else if (ACTION_IDENTIFY.equals(action)) {
                this.identify(args.getJSONObject(0), callbackContext);
                return true;
            } else if (ACTION_TRACK.equals(action)) {
                this.track(args.getJSONObject(0), callbackContext);
                return true;
            } else if (ACTION_SCREEN.equals(action)) {
                this.screen(args.getJSONObject(0), callbackContext);
                return true;
            } else if (ACTION_GROUP.equals(action)) {
                this.group(args.getJSONObject(0), callbackContext);
                return true;
            } else if (ACTION_ALIAS.equals(action)) {
                this.alias(args.getJSONObject(0), callbackContext);
                return true;
            } else if (ACTION_GET_ANONYMOUS_ID.equals(action)) {
                this.getAnonymousId(callbackContext);
                return true;
            } else if (ACTION_RESET.equals(action)) {
                this.reset(callbackContext);
                return true;
            }
        }

        return false;
    }

    private void startWithConfiguration(String id, JSONObject obj, CallbackContext callbackContext) {
        Analytics.Builder builder;
        String logLevel;
        Options options;

        try {
            if (null != id && id.length() > 0) {
                builder = new Analytics.Builder(cordova.getActivity().getApplicationContext(), id);

                if (obj != null) {
                    if (obj.has("trackApplicationLifecycleEvents")) {
                        if (obj.optBoolean("trackApplicationLifecycleEvents")) {
                            builder.trackApplicationLifecycleEvents();
                        }
                    }
                    if (obj.has("recordScreenViews")) {
                        if (obj.optBoolean("recordScreenViews")) {
                            builder.recordScreenViews();
                        }
                    }
                    if (obj.has("trackAttributionInformation")) {
                        if (obj.optBoolean("trackAttributionInformation")) {
                            builder.trackAttributionInformation();
                        }
                    }
                    // android only
                    if (obj.has("collectDeviceId")) {
                        builder.collectDeviceId(obj.optBoolean("collectDeviceId"));
                    }
                    // android only
                    if (obj.has("flushQueueSize")) {
                        builder.flushQueueSize(obj.optInt("flushQueueSize"));
                    }
                    // android only and flushInterval value should be seconds base.
                    if (obj.has("flushInterval")) {
                        builder.flushInterval(obj.optInt("flushInterval"), TimeUnit.SECONDS);
                    }
                    // android only
                    if (obj.has("tag")) {
                        builder.tag(obj.optString("tag"));
                    }
                    // android only
                    if (obj.has("logLevel")) {
                        logLevel = obj.optString("logLevel", "");
                        if (logLevel.equals("NONE")) {
                            builder.logLevel(Analytics.LogLevel.NONE);
                        } else if (logLevel.equals("INFO")) {
                            builder.logLevel(Analytics.LogLevel.INFO);
                        } else if (logLevel.equals("DEBUG")) {
                            builder.logLevel(Analytics.LogLevel.DEBUG);
                        } else if (logLevel.equals("VERBOSE")) {
                            builder.logLevel(Analytics.LogLevel.VERBOSE);
                        }
                    }
                    if (obj.has("launchOptions")) {
                        options = toOptions(obj.optJSONObject("launchOptions"));
                        builder.defaultOptions(options);
                    }
                    if (obj.has("enableBrazeIntegration") && obj.optBoolean("enableBrazeIntegration") == true) {
                        builder.use(AppboyIntegration.FACTORY);
                    }
                    // middleware, connectionFactory, optOut are not currently supported.
                }

                Analytics analytics = builder.build();
                // Set the initialized instance as a globally accessible instance.
                Analytics.setSingletonInstance(analytics);

                callbackContext.success("Segment configuration started");
            } else {
                callbackContext.error("Key is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void identify(JSONObject obj, CallbackContext callbackContext) {
        String userId;
        Options options;
        Traits traits;

        try {
            if (obj != null) {
                // userId is optional. If null, the anonymous id will be used
                userId = obj.optString("userId", null);
                traits = toTraits(obj.optJSONObject("traits"));
                options = toOptions(obj.optJSONObject("options"));

                Analytics.with(cordova.getActivity().getApplicationContext())
                        .identify(userId, traits, options);

                callbackContext.success("track identify for " + userId);
            } else {
                callbackContext.error("Segment object is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void track(JSONObject obj, CallbackContext callbackContext) {
        String event;
        Properties properties;
        Options options;

        try {
            if (obj != null) {
                event = obj.optString("event");

                if (event != null && event.length() > 0) {
                    properties = toProperties(obj.optJSONObject("properties"));
                    options = toOptions(obj.optJSONObject("options"));

                    Analytics.with(cordova.getActivity().getApplicationContext())
                            .track(event, properties, options);

                    callbackContext.success("track event for " + event);
                } else {
                    callbackContext.error("The name of the event is required.");
                }
            } else {
                callbackContext.error("Segment object is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void screen(JSONObject obj, CallbackContext callbackContext) {
        String category;
        String name;
        Properties properties;
        Options options;

        try {
            if (obj != null) {
                category = obj.optString("category", null);
                name = obj.optString("name", null);
                properties = toProperties(obj.optJSONObject("properties"));
                options = toOptions(obj.optJSONObject("options"));

                Analytics.with(cordova.getActivity().getApplicationContext())
                        .screen(category, name, properties, options);

                callbackContext.success("track screen for " + name);
            } else {
                callbackContext.error("Segment object is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void group(JSONObject obj, CallbackContext callbackContext) {
        String userId;
        String groupId;
        Traits traits;
        Options options;

        try {
            if (obj != null) {
                userId = obj.optString("userId");
                groupId = obj.optString("groupId");

                if (null != userId && userId.length() > 0 && null != groupId && groupId.length() > 0) {
                    traits = toTraits(obj.optJSONObject("traits"));
                    options = toOptions(obj.optJSONObject("options"));

                    // The userId is used according to the document (https://segment.com/docs/sources/mobile/android/#group)
                    // However, the userId is not used for actual API.
                    // https://github.com/segmentio/analytics-android/blob/master/analytics/src/main/java/com/segment/analytics/Analytics.java#L531
                    Analytics.with(cordova.getActivity().getApplicationContext())
                            .group(groupId, traits, options);

                    callbackContext.success("group " + groupId + " for user " + userId);
                } else {
                    callbackContext.error("userId and groupId are required.");
                }
            } else {
                callbackContext.error("Segment object is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void alias(JSONObject obj, CallbackContext callbackContext) {
        String newId;
        Options options;

        try {
            if (obj != null) {

                newId = obj.optString("newId");

                if (null != newId && newId.length() > 0) {
                    options = toOptions(obj.optJSONObject("options"));

                    Analytics.with(cordova.getActivity().getApplicationContext())
                            .alias(newId, options);

                    callbackContext.success("alias for " + newId);
                } else {
                    callbackContext.error("newId is required.");
                }
            } else {
                callbackContext.error("Segment object is required.");
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void getAnonymousId(CallbackContext callbackContext) {
        String anonymousId;

        try {
            anonymousId = Analytics.with(cordova.getActivity().getApplicationContext())
                    .getAnalyticsContext()
                    .traits()
                    .anonymousId();

            callbackContext.success(anonymousId);

        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    private void reset(CallbackContext callbackContext) {
        try {
            Analytics.with(cordova.getActivity().getApplicationContext())
                    .reset();

            callbackContext.success();
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }
    }

    /**
     * @see <a href="https://github.com/segmentio/analytics-android/blob/master/analytics/src/main/java/com/segment/analytics/Traits.java">Traits</a>
     */
    private Traits toTraits(JSONObject traitsObj) {
        Traits traits = null;

        Traits.Address address;
        int age;
        String avatar;
        String birthday;
        String createdAt;
        String description;
        String email;
        long employees;
        String fax;
        String firstName;
        String gender;
        String industry;
        String lastName;
        String name;
        String phone;
        String title;
        String username;
        String website;

        // to construct Traits.Address object
        JSONObject addressObj;
        String city;
        String country;
        String postalCode;
        String state;
        String street;

        // to construct custom properties
        Iterator<?> traitList;
        String key;
        Object obj;

        try {
            if (null != traitsObj) {
                traits = new Traits();

                addressObj = traitsObj.optJSONObject("address");

                if (null != addressObj) {
                    address = new Traits.Address();

                    city = addressObj.optString("city", null);
                    if (city != null) {
                        address.putCity(city);
                    }
                    country = addressObj.optString("country", null);
                    if (country != null) {
                        address.putCountry(country);
                    }
                    postalCode = addressObj.optString("postalCode", null);
                    if (postalCode != null) {
                        address.putPostalCode(postalCode);
                    }
                    state = addressObj.optString("state", null);
                    if (state != null) {
                        address.putState(state);
                    }
                    street = addressObj.optString("street", null);
                    if (street != null) {
                        address.putStreet(street);
                    }
                    traits.putAddress(address);
                }

                age = traitsObj.optInt("age", Integer.MIN_VALUE);
                if (age != Integer.MIN_VALUE) {
                    traits.putAge(age);
                }
                avatar = traitsObj.optString("avatar", null);
                if (avatar != null) {
                    traits.putAvatar(avatar);
                }
                birthday = traitsObj.optString("birthday", null);
                if (birthday != null) {
                    traits.putBirthday(formatter.parse(birthday));
                }
                createdAt = traitsObj.optString("createdAt", null);
                if (createdAt != null) {
                    traits.putCreatedAt(createdAt);
                }
                description = traitsObj.optString("description", null);
                if (description != null) {
                    traits.putDescription(description);
                }
                email = traitsObj.optString("email", null);
                if (email != null) {
                    traits.putEmail(email);
                }
                employees = traitsObj.optLong("employees", Integer.MIN_VALUE);
                if (employees != Integer.MIN_VALUE) {
                    traits.putEmployees(employees);
                }
                fax = traitsObj.optString("fax", null);
                if (fax != null) {
                    traits.putFax(fax);
                }
                firstName = traitsObj.optString("firstName", null);
                if (firstName != null) {
                    traits.putFirstName(firstName);
                }
                gender = traitsObj.optString("gender", null);
                if (gender != null) {
                    traits.putGender(gender);
                }
                industry = traitsObj.optString("industry", null);
                if (industry != null) {
                    traits.putIndustry(industry);
                }
                lastName = traitsObj.optString("lastName", null);
                if (lastName != null) {
                    traits.putLastName(lastName);
                }
                name = traitsObj.optString("name", null);
                if (name != null) {
                    traits.putName(name);
                }
                phone = traitsObj.optString("phone", null);
                if (phone != null) {
                    traits.putPhone(phone);
                }
                title = traitsObj.optString("title", null);
                if (title != null) {
                    traits.putTitle(title);
                }
                username = traitsObj.optString("username", null);
                if (username != null) {
                    traits.putUsername(username);
                }
                website = traitsObj.optString("website", null);
                if (website != null) {
                    traits.putWebsite(website);
                }

                traitList = traitsObj.keys();

                // Iterate traits to find non-predefined traits
                while (traitList.hasNext()) {
                    key = (String) traitList.next();

                    if (!traits.containsKey(key)) {
                        obj = traitsObj.get(key);

                        if (obj instanceof Integer) {
                            traits.putValue(key, traitsObj.optInt(key));
                        } else if (obj instanceof Long) {
                            traits.putValue(key, traitsObj.optLong(key));
                        } else if (obj instanceof Double) {
                            traits.putValue(key, traitsObj.optDouble(key));
                        } else if (obj instanceof String) {
                            traits.putValue(key, traitsObj.optString(key));
                        } else if (obj instanceof Boolean) {
                            traits.putValue(key, traitsObj.optBoolean(key));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }

        return traits;
    }

    /**
     * @see <a href="https://github.com/segmentio/analytics-android/blob/master/analytics/src/main/java/com/segment/analytics/Properties.java">Properties</a>
     */
    private Properties toProperties(JSONObject propertiesObj) {
        Properties properties = null;

        // predefined properties
        double revenue;
        String currency;
        double value;
        String path;
        String referrer;
        String title;
        String url;
        String name;
        String category;
        String sku;
        double price;
        String id;
        String orderId;
        double total;
        double subtotal;
        double shipping;
        double tax;
        double discount;
        String coupon;
        List<Properties.Product> products;
        Properties.Product product;

        // to construct Properties.Product object
        JSONArray productList;
        JSONObject prod;
        String prodId;
        String prodName;
        String prodCategory;
        String prodVariant;
        double prodPrice;

        // to construct custom properties
        Iterator<?> propertyList;
        String key;
        Object obj;

        try {
            if (propertiesObj != null) {
                properties = new Properties();

                revenue = propertiesObj.optDouble("revenue", Double.MIN_VALUE);
                if (revenue != Double.MIN_VALUE) {
                    properties.putRevenue(revenue);
                }
                currency = propertiesObj.optString("currency", null);
                if (currency != null) {
                    properties.putCurrency(currency);
                }
                value = propertiesObj.optDouble("value", Double.MIN_VALUE);
                if (value != Double.MIN_VALUE) {
                    properties.putValue(value);
                }
                path = propertiesObj.optString("path", null);
                if (path != null) {
                    properties.putPath(path);
                }
                referrer = propertiesObj.optString("referrer", null);
                if (referrer != null) {
                    properties.putReferrer(referrer);
                }
                title = propertiesObj.optString("title", null);
                if (title != null) {
                    properties.putTitle(title);
                }
                url = propertiesObj.optString("url", null);
                if (url != null) {
                    properties.putUrl(url);
                }
                name = propertiesObj.optString("name", null);
                if (name != null) {
                    properties.putName(name);
                }
                category = propertiesObj.optString("category", null);
                if (category != null) {
                    properties.putCategory(category);
                }
                sku = propertiesObj.optString("sku", null);
                if (sku != null) {
                    properties.putSku(sku);
                }
                price = propertiesObj.optDouble("price", Double.MIN_VALUE);
                if (price != Double.MIN_VALUE) {
                    properties.putPrice(price);
                }
                id = propertiesObj.optString("id", null);
                if (id != null) {
                    properties.putProductId(id);
                }
                orderId = propertiesObj.optString("orderId", null);
                if (orderId != null) {
                    properties.putOrderId(orderId);
                }
                total = propertiesObj.optDouble("total", Double.MIN_VALUE);
                if (total != Double.MIN_VALUE) {
                    properties.putTotal(total);
                }
                subtotal = propertiesObj.optDouble("subtotal", Double.MIN_VALUE);
                if (subtotal != Double.MIN_VALUE) {
                    properties.putSubtotal(subtotal);
                }
                shipping = propertiesObj.optDouble("shipping", Double.MIN_VALUE);
                if (shipping != Double.MIN_VALUE) {
                    properties.putShipping(shipping);
                }
                tax = propertiesObj.optDouble("tax", Double.MIN_VALUE);
                if (tax != Double.MIN_VALUE) {
                    properties.putTax(tax);
                }
                discount = propertiesObj.optDouble("discount", Double.MIN_VALUE);
                if (discount != Double.MIN_VALUE) {
                    properties.putDiscount(discount);
                }
                coupon = propertiesObj.optString("coupon", null);
                if (coupon != null) {
                    properties.putCoupon(coupon);
                }
                if (propertiesObj.has("repeat")) {
                    properties.putRepeatCustomer(propertiesObj.optBoolean("repeat"));
                }

                productList = propertiesObj.optJSONArray("products");

                if (productList != null && productList.length() > 0) {
                    products = new ArrayList<Properties.Product>();

                    for (int i = 0; i < productList.length(); i++) {
                        prod = productList.getJSONObject(i);

                        prodId = prod.optString("id", null);
                        prodPrice = prod.optDouble("price", 0);

                        product = new Properties.Product(prodId, null, prodPrice);

                        prodName = prod.optString("name", null);
                        if (prodName != null) {
                            product.putName(prodName);
                        }
                        // category, variant, and position are used for Google Analytics product
                        prodCategory = prod.optString("category", null);
                        if (prodCategory != null) {
                            product.putValue("category", prodCategory);
                        }
                        prodVariant = prod.optString("variant", null);
                        if (prodVariant != null) {
                            product.putValue("variant", prodVariant);
                        }
                        product.putValue("position", prod.optInt("position", 1));

                        products.add(product);
                    }

                    properties.putProducts(products.toArray(new Properties.Product[products.size()]));
                }

                propertyList = propertiesObj.keys();

                // Iterate properties to find non-predefined properties
                while (propertyList.hasNext()) {
                    key = (String) propertyList.next();

                    if (!properties.containsKey(key)) {
                        obj = propertiesObj.get(key);

                        if (obj instanceof Integer) {
                            properties.putValue(key, propertiesObj.optInt(key));
                        } else if (obj instanceof Long) {
                            properties.putValue(key, propertiesObj.optLong(key));
                        } else if (obj instanceof Double) {
                            properties.putValue(key, propertiesObj.optDouble(key));
                        } else if (obj instanceof String) {
                            properties.putValue(key, propertiesObj.optString(key));
                        } else if (obj instanceof Boolean) {
                            properties.putValue(key, propertiesObj.optBoolean(key));
                        }
                    }
                }
            }

        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }

        return properties;
    }

    /**
     * @see <a href="https://github.com/segmentio/analytics-android/blob/master/analytics/src/main/java/com/segment/analytics/Options.java">Options</a>
     */
    private Options toOptions(JSONObject optionsObj) {
        Options options = null;

        Iterator<?> keys;
        String key;
        Object value;
        Map<String, Object> integrationOptions;
        JSONObject integrationInputs;
        Iterator<String> integrationInputsItr;
        String integrationOptionKey;
        Object integrationOptionValue;

        try {
            if (optionsObj != null) {
                options = new Options();

                keys = optionsObj.keys();

                while (keys.hasNext()) {
                    key = (String) keys.next();
                    value = optionsObj.get(key);
                    if (value instanceof JSONObject) {

                        integrationOptions = new HashMap<String, Object>();

                        integrationInputs = optionsObj.optJSONObject(key);

                        integrationInputsItr = integrationInputs.keys();

                        while (integrationInputsItr.hasNext()) {
                            integrationOptionKey = integrationInputsItr.next();
                            integrationOptionValue = integrationInputs.get(integrationOptionKey);
                            integrationOptions.put(integrationOptionKey, integrationOptionValue);
                        }
                        options.setIntegrationOptions(key, integrationOptions);
                    } else if (value instanceof Boolean) {
                        options.setIntegration(key, optionsObj.optBoolean(key));
                    }
                }
            }
        } catch (Exception ex) {
            Log.getStackTraceString(ex);
        }

        return options;
    }
}
