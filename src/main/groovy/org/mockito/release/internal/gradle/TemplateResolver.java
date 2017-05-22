package org.mockito.release.internal.gradle;

import org.mockito.release.internal.util.ArgumentValidation;

import java.util.HashMap;
import java.util.Map;

public class TemplateResolver {

    private final String text;

    private final Map<String, String> properties;

    public TemplateResolver(String text){
        this.text = text;
        this.properties = new HashMap<String, String>();
    }

    public TemplateResolver withProperty(String key, String value){
        ArgumentValidation.notNull(key, "key", value, "value");
        properties.put(key, value);
        return this;
    }

    public String resolve(){
        String result = text;
        for(Map.Entry<String, String> property : properties.entrySet()){
            result = result.replace(expressionFromKey(property.getKey()), property.getValue());
        }
        return result;
    }

    private String expressionFromKey(String key){
         return "@" + key + "@";
    }
}
