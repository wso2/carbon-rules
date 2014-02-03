/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.rule.mediator.config;

import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMAbstractFactory;

public class RuleMediatorConfig {

    private String backendRuntimeFactory;
    private Source source;
    private Target target;
    private RuleSet ruleSet;
    private Input input;
    private Output output;

    public RuleMediatorConfig() {
        this.source = new Source();
        this.target = new Target();
        this.ruleSet = new RuleSet();
        this.input = new Input();
        this.output = new Output();
    }

    public OMElement toOM(){
        
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMElement ruleMediatorElement = omFactory.createOMElement(Constants.RULE_CONF_ELE_RULE,
                                                              Constants.RULE_CONF_NAMESPACE,
                                                              Constants.RULE_CONF_NAMESPACE_PREFIX);
        if (this.source != null){
            ruleMediatorElement.addChild(this.source.toOM());
        }

        if (this.target != null){
            ruleMediatorElement.addChild(this.target.toOM());
        }

        if (this.ruleSet != null){
            ruleMediatorElement.addChild(this.ruleSet.toOM());
        }

        if (this.input != null){
            ruleMediatorElement.addChild(this.input.toOM());
        }

        if (this.output != null){
            ruleMediatorElement.addChild(this.output.toOM());
        }

        return ruleMediatorElement;

    }

    public String getBackendRuntimeFactory() {
        return backendRuntimeFactory;
    }

    public void setBackendRuntimeFactory(String backendRuntimeFactory) {
        this.backendRuntimeFactory = backendRuntimeFactory;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }
}
