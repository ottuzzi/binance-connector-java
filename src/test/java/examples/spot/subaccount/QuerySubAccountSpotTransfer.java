package examples.spot.subaccount;

import java.util.LinkedHashMap;

import com.binance.connector.client.impl.SpotClientImpl;

import examples.PrivateConfig;

public final class QuerySubAccountSpotTransfer {
    private QuerySubAccountSpotTransfer() {
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        SpotClientImpl client = new SpotClientImpl(PrivateConfig.API_KEY, PrivateConfig.SECRET_KEY);
        String result = client.createSubAccount().spotTransferHistory(parameters);
        System.out.println(result);
    }
}