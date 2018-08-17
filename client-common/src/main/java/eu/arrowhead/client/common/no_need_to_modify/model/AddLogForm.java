/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.client.common.no_need_to_modify.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Flavio
 */
public class AddLogForm {

    private int id;
    private ArrowheadSystem consumer;
    private ArrowheadSystem provider;
    private Map<String, String> parameters = new HashMap<>();

    public AddLogForm() {
    }

    public AddLogForm(ArrowheadSystem consumer, ArrowheadSystem provider, Map<String, String> parameters) {
        this.consumer = consumer;
        this.provider = provider;
        this.parameters = parameters;
    }

    public ArrowheadSystem getConsumer() {
        return consumer;
    }

    public void setConsumer(ArrowheadSystem consumer) {
        this.consumer = consumer;
    }

    public ArrowheadSystem getProvider() {
        return provider;
    }

    public void setProvider(ArrowheadSystem provider) {
        this.provider = provider;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
