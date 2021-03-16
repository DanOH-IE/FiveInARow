package com.dohalloran.code.challenge.fiveinarowclient.config;

import com.dohalloran.code.challenge.fiveinarow.api.JoinApi;
import com.dohalloran.code.challenge.fiveinarow.api.MoveApi;
import com.dohalloran.code.challenge.fiveinarow.api.UpdateApi;
import com.dohalloran.code.challenge.fiveinarow.client.ApiClient;
import com.dohalloran.code.challenge.fiveinarowclient.controller.FiveInARowClient;
import com.dohalloran.code.challenge.fiveinarowclient.controller.FiveInARowController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Value("${fiveInARow.host}")
    private String host;

    @Value("${fiveInARow.port}")
    private int port;

    @Bean
    public ApiClient apiClient() {
        return new ApiClient()
            .setHost(host)
            .setPort(port);
    }

    @Bean
    public MoveApi moveApi(ApiClient apiClient) {
        return new MoveApi(apiClient);
    }

    @Bean
    public UpdateApi updateApi(ApiClient apiClient) {
        return new UpdateApi(apiClient);
    }

    @Bean
    public JoinApi joinApi(ApiClient apiClient) {
        return new JoinApi(apiClient);
    }

    @Bean
    public FiveInARowClient fiveInARowClient() {
        return new FiveInARowClient();
    }

    @Bean
    public FiveInARowController fiveInARowController(){
        return new FiveInARowController();
    }
}
