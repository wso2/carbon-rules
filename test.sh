cd /Users/dulanjali/Documents/repos/carbon-rules-new-migration/carbon-rules/components/rule/org.wso2.carbon.rule.backend;
#/Users/dulanjali/Documents/Software/apache-maven-3.5.4/bin/mvn -s /Users/dulanjali/Documents/repos/settings_xml/umt_settings.xml clean install -Dmaven.test.skip=true;
/Users/dulanjali/Documents/Software/apache-maven-3.5.4/bin/mvn -s /Users/dulanjali/Documents/repos/settings_xml/umt_settings.xml clean install -Dmaven.test.skip=true;
cd target;
mv org.wso2.carbon.rule.backend-4.5.6.jar org.wso2.carbon.rule.backend_4.5.6.jar;
rm /Users/dulanjali/Documents/patches/2023/MI/patches/rules/wso2mi-4.2.0.migration/wso2/components/plugins/org.wso2.carbon.rule.backend_4.5.6.jar;
cp org.wso2.carbon.rule.backend_4.5.6.jar /Users/dulanjali/Documents/patches/2023/MI/patches/rules/wso2mi-4.2.0.migration/wso2/components/plugins/;