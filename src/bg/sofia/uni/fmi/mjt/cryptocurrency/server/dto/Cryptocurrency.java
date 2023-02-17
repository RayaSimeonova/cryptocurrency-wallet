package bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto;

import com.google.gson.annotations.SerializedName;

public record Cryptocurrency(@SerializedName("asset_id") String assetId, String name,
                             @SerializedName("type_is_crypto") int isCrypto,
                             @SerializedName("data_symbols_count") int dataSymbolsCount,
                             @SerializedName("volume_1hrs_usd") double hourVolumeUsd,
                             @SerializedName("volume_1day_usd") double dayVolumeUsd,
                             @SerializedName("volume_1mth_usd") double monthVolumeUsd,
                             @SerializedName("price_usd") double priceUsd) {
    @Override
    public String toString() {
        return "{" +
            "assetId='" + assetId + '\'' +
            ", name='" + name + '\'' +
            ", dataSymbolsCount=" + dataSymbolsCount +
            ", hourVolumeUsd=" + hourVolumeUsd +
            ", dayVolumeUsd=" + dayVolumeUsd +
            ", monthVolumeUsd=" + monthVolumeUsd +
            ", priceUsd=" + priceUsd +
            '}';
    }
}
