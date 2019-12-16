package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import cn.tillusory.sdk.bean.TiFilterEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/26.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiFilter {
    NO_FILTER(TiFilterEnum.NO_FILTER, R.drawable.ic_ti_filter_0),
    SKETCH_FILTER(TiFilterEnum.SKETCH_FILTER, R.drawable.ic_ti_filter_1),
    SOBEL_EDGE_FILTER(TiFilterEnum.SOBEL_EDGE_FILTER, R.drawable.ic_ti_filter_2),
    CARTOON_FILTER(TiFilterEnum.CARTOON_FILTER, R.drawable.ic_ti_filter_3),
    EMBOSS_FILTER(TiFilterEnum.EMBOSS_FILTER, R.drawable.ic_ti_filter_4),
    FILM_FILTER(TiFilterEnum.FILM_FILTER, R.drawable.ic_ti_filter_5),
    PIXELATION_FILTER(TiFilterEnum.PIXELATION_FILTER, R.drawable.ic_ti_filter_6),
    HALFTONE_FILTER(TiFilterEnum.HALFTONE_FILTER, R.drawable.ic_ti_filter_7),
    CROSSHATCH_FILTER(TiFilterEnum.CROSSHATCH_FILTER, R.drawable.ic_ti_filter_8),
    NASHVILLE_FILTER(TiFilterEnum.NASHVILLE_FILTER, R.drawable.ic_ti_filter_9),
    COFFEE_FILTER(TiFilterEnum.COFFEE_FILTER, R.drawable.ic_ti_filter_10),
    CHOCOLATE_FILTER(TiFilterEnum.CHOCOLATE_FILTER, R.drawable.ic_ti_filter_11),
    COCO_FILTER(TiFilterEnum.COCO_FILTER, R.drawable.ic_ti_filter_12),
    DELICIOUS_FILTER(TiFilterEnum.DELICIOUS_FILTER, R.drawable.ic_ti_filter_13),
    FIRSTLOVE_FILTER(TiFilterEnum.FIRSTLOVE_FILTER, R.drawable.ic_ti_filter_14),
    FOREST_FILTER(TiFilterEnum.FOREST_FILTER, R.drawable.ic_ti_filter_15),
    GLOSSY_FILTER(TiFilterEnum.GLOSSY_FILTER, R.drawable.ic_ti_filter_16),
    GRASS_FILTER(TiFilterEnum.GRASS_FILTER, R.drawable.ic_ti_filter_17),
    HOLIDAY_FILTER(TiFilterEnum.HOLIDAY_FILTER, R.drawable.ic_ti_filter_18),
    KISS_FILTER(TiFilterEnum.KISS_FILTER, R.drawable.ic_ti_filter_19),
    LOLITA_FILTER(TiFilterEnum.LOLITA_FILTER, R.drawable.ic_ti_filter_20),
    MEMORY_FILTER(TiFilterEnum.MEMORY_FILTER, R.drawable.ic_ti_filter_21),
    MOUSSE_FILTER(TiFilterEnum.MOUSSE_FILTER, R.drawable.ic_ti_filter_22),
    NORMAL_FILTER(TiFilterEnum.NORMAL_FILTER, R.drawable.ic_ti_filter_23),
    OXGEN_FILTER(TiFilterEnum.OXGEN_FILTER, R.drawable.ic_ti_filter_24),
    PLATYCODON_FILTER(TiFilterEnum.PLATYCODON_FILTER, R.drawable.ic_ti_filter_25),
    RED_FILTER(TiFilterEnum.RED_FILTER, R.drawable.ic_ti_filter_26),
    SUNLESS_FILTER(TiFilterEnum.SUNLESS_FILTER, R.drawable.ic_ti_filter_27),
    PINCH_DISTORTION_FILTER(TiFilterEnum.PINCH_DISTORTION_FILTER, R.drawable.ic_ti_filter_28),
    KUWAHARA_FILTER(TiFilterEnum.KUWAHARA_FILTER, R.drawable.ic_ti_filter_29),
    POSTERIZE_FILTER(TiFilterEnum.POSTERIZE_FILTER, R.drawable.ic_ti_filter_30),
    SWIRL_DISTORTION_FILTER(TiFilterEnum.SWIRL_DISTORTION_FILTER, R.drawable.ic_ti_filter_31),
    VIGNETTE_FILTER(TiFilterEnum.VIGNETTE_FILTER, R.drawable.ic_ti_filter_32),
    ZOOM_BLUR_FILTER(TiFilterEnum.ZOOM_BLUR_FILTER, R.drawable.ic_ti_filter_33),
    POLKA_DOT_FILTER(TiFilterEnum.POLKA_DOT_FILTER, R.drawable.ic_ti_filter_34),
    POLAR_PIXELLATE_FILTER(TiFilterEnum.POLAR_PIXELLATE_FILTER, R.drawable.ic_ti_filter_35),
    GLASS_SPHERE_REFRACTION_FILTER(TiFilterEnum.GLASS_SPHERE_REFRACTION_FILTER, R.drawable.ic_ti_filter_36),
    SOLARIZE_FILTER(TiFilterEnum.SOLARIZE_FILTER, R.drawable.ic_ti_filter_37),
    INK_WASH_PAINTING_FILTER(TiFilterEnum.INK_WASH_PAINTING_FILTER, R.drawable.ic_ti_filter_38),
    ARABICA_FILTER(TiFilterEnum.ARABICA_FILTER, R.drawable.ic_ti_filter_39),
    AVA_FILTER(TiFilterEnum.AVA_FILTER, R.drawable.ic_ti_filter_40),
    AZREAL_FILTER(TiFilterEnum.AZREAL_FILTER, R.drawable.ic_ti_filter_41),
    BOURBON_FILTER(TiFilterEnum.BOURBON_FILTER, R.drawable.ic_ti_filter_42),
    BYERS_FILTER(TiFilterEnum.BYERS_FILTER, R.drawable.ic_ti_filter_43),
    CHEMICAL_FILTER(TiFilterEnum.CHEMICAL_FILTER, R.drawable.ic_ti_filter_44),
    CLAYTON_FILTER(TiFilterEnum.CLAYTON_FILTER, R.drawable.ic_ti_filter_45),
    CLOUSEAU_FILTER(TiFilterEnum.CLOUSEAU_FILTER, R.drawable.ic_ti_filter_46),
    COBI_FILTER(TiFilterEnum.COBI_FILTER, R.drawable.ic_ti_filter_47),
    CONTRAIL_FILTER(TiFilterEnum.CONTRAIL_FILTER, R.drawable.ic_ti_filter_48),
    CUBICLE_FILTER(TiFilterEnum.CUBICLE_FILTER, R.drawable.ic_ti_filter_49),
    DJANGO_FILTER(TiFilterEnum.DJANGO_FILTER, R.drawable.ic_ti_filter_50);

    private TiFilterEnum filterEnum;
    private int imageId;

    TiFilter(TiFilterEnum filterEnum, @DrawableRes int imageId) {
        this.filterEnum = filterEnum;
        this.imageId = imageId;
    }

    public TiFilterEnum getFilterEnum() {
        return filterEnum;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(filterEnum.getStringId());
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }
}
