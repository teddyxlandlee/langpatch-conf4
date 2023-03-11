package xland.mcmod.enchlevellangpatch.ext.conf4;

import org.jetbrains.annotations.Nullable;
import xland.mcmod.enchlevellangpatch.impl.ConfigProvider;

@SuppressWarnings("unused") // referenced from LangPatch itself
public class LangPatchConfigHooks implements ConfigProvider {
    public LangPatchConfigHooks() {
        LangPatchConfig.init();
    }

    @Override
    @Nullable
    public String getEnchantmentConfig() {
        return LangPatchConfig.getInstance().getEnchantmentCfg();
    }

    @Override
    @Nullable
    public String getPotionConfig() {
        return LangPatchConfig.getInstance().getPotionCfg();
    }
}
