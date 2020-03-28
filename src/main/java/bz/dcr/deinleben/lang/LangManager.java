package bz.dcr.deinleben.lang;

import bz.dcr.deinleben.DeinLebenPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LangManager {

    private Configuration langConfig;

    private String cachedPrefix;
    private SimpleDateFormat dateFormat;

    private DecimalFormat numberFormat;
    private DecimalFormatSymbols numberFormatSymbols;


    public LangManager(Configuration langConfig) {
        this.langConfig = langConfig;

        cachedPrefix = ChatColor.translateAlternateColorCodes('&', langConfig.getString(LangKey.PREFIX));
        dateFormat = loadDateFormat();

        numberFormat = loadNumberFormat();
        numberFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        numberFormatSymbols.setDecimalSeparator(
                langConfig.getString(LangKey.NUMBER_FORMAT_DECIMAL_SEPARATOR).charAt(0)
        );
        numberFormatSymbols.setGroupingSeparator(
                langConfig.getString(LangKey.NUMBER_FORMAT_GROUPING_SEPARATOR).charAt(0)
        );
        numberFormat.setDecimalFormatSymbols(numberFormatSymbols);
    }


    /**
     * Returns a color formatted message with or without the prefix
     *
     * @param key    The key of the message
     * @param prefix Whether the prefix should be added or not
     * @return Color formatted message with or without the prefix
     */
    public String getMessage(String key, boolean prefix) {
        return getMessage(key, prefix, new HashMap<>());
    }

    /**
     * Returns a color formatted with injected placeholders and with or without the prefix
     *
     * @param key          The key of the message
     * @param prefix       Whether the prefix should be added or not
     * @param placeholders A Map with key and value of the type String containing all placeholders
     * @return Color formatted message with injected placeholders and with or without the prefix
     */
    public String getMessage(String key, boolean prefix, Map<String, String> placeholders) {
        // Get raw message
        String message = getRawMessage(key);

        // Show missing message
        if (message == null) {
            DeinLebenPlugin.getInstance().getLogger().warning("Could not find message '" + key + "'!");
            message = "§cNachricht konnte nicht gefunden werden.";
        }

        // Inject placeholders
        message = injectPlaceholders(message, placeholders);

        // Finally format message
        return formatMessage(message, prefix);
    }


    /**
     * Returns a color formatted with injected placeholders and with or without the prefix from a list
     *
     * @param key    The key of the message list
     * @param prefix Whether the prefix should be added or not
     * @return Color formatted message with injected placeholders and with or without the prefix
     */
    public String getMessageFromList(String key, boolean prefix) {
        return getMessageFromList(key, prefix, new HashMap<>());
    }

    /**
     * Returns a color formatted with injected placeholders and with or without the prefix from a list
     *
     * @param key          The key of the message list
     * @param prefix       Whether the prefix should be added or not
     * @param placeholders A Map with key and value of the type String containing all placeholders
     * @return Color formatted message with injected placeholders and with or without the prefix
     */
    public String getMessageFromList(String key, boolean prefix, Map<String, String> placeholders) {
        // Get raw message
        String message = getRawMessageFromList(key);

        // Inject placeholders
        message = injectPlaceholders(message, placeholders);

        // Finally format message
        return formatMessage(message, prefix);
    }


    private String injectPlaceholders(String message, Map<String, String> placeholders) {
        String formattedMessage = message;

        // Inject placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            formattedMessage = formattedMessage.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return formattedMessage;
    }

    private String formatMessage(String message, boolean prefix) {
        // Add line breaks
        message = message.replace("\\n", "\n");

        return prefix ?
                cachedPrefix + ChatColor.translateAlternateColorCodes('&', message) :
                ChatColor.translateAlternateColorCodes('&', message);
    }


    /**
     * Returns the raw message without color formatting and placeholder injection
     *
     * @param key The key of the message
     * @return Raw message without color formatting and placeholder injection
     */
    private String getRawMessage(String key) {
        return langConfig.getString(key);
    }

    /**
     * Returns the raw message from a list without color formatting and placeholder injection
     *
     * @param key The key of the message list
     * @return Raw message from the list without color formatting and placeholder injection
     */
    private String getRawMessageFromList(String key) {
        return String.join("\n", langConfig.getStringList(key));
    }


    /**
     * Formats a {@link Date} using the configured format
     *
     * @param date The date to format
     * @return The formatted String representation of the {@link Date}
     */
    public String formatDate(Date date) {
        return dateFormat.format(date);
    }


    /**
     * Formats a number using the configured format
     *
     * @param number The number to format
     * @return The formatted String representation of the number
     */
    public String formatNumber(BigDecimal number) {
        return numberFormat.format(number);
    }

    /**
     * Formats a number using the configured format
     *
     * @param number The number to format
     * @return The formatted String representation of the number
     */
    public String formatNumber(Float number) {
        return numberFormat.format(number);
    }

    /**
     * Formats a number using the configured format
     *
     * @param number The number to format
     * @return The formatted String representation of the number
     */
    public String formatNumber(Double number) {
        return numberFormat.format(number);
    }

    /**
     * Formats a number using the configured format
     *
     * @param number The number to format
     * @return The formatted String representation of the number
     */
    public String formatNumber(Short number) {
        return numberFormat.format(number);
    }

    /**
     * Formats a number using the configured format
     *
     * @param number The number to format
     * @return The formatted String representation of the number
     */
    public String formatNumber(Integer number) {
        return NumberFormat.getInstance(Locale.GERMANY).format(number);
    }

    public ItemStack formatItem(String key, Material material) {
        return formatItem(key, material, new HashMap<>());
    }

    public ItemStack formatItem(String key, Material material, Map<String, String> placeholders) {
        return formatItem(key, new ItemStack(material), placeholders);
    }

    public ItemStack formatItem(String key, ItemStack itemStack) {
        return formatItem(key, itemStack, new HashMap<>());
    }

    public ItemStack formatItem(String key, ItemStack itemStack, Map<String, String> placeholders) {
        final String name = getMessage(key + ".Name", false, placeholders);
        List<String> lore = langConfig.getStringList(key + ".Lore");

        // Inject placeholders into lore
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor
                    .translateAlternateColorCodes('&', injectPlaceholders(lore.get(i), placeholders)));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private SimpleDateFormat loadDateFormat() {
        return new SimpleDateFormat(langConfig.getString(LangKey.DATE_FORMAT));
    }

    private DecimalFormat loadNumberFormat() {
        return new DecimalFormat(langConfig.getString(LangKey.NUMBER_FORMAT));
    }

}
