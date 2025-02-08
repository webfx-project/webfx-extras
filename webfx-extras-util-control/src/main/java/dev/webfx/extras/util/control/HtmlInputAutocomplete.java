package dev.webfx.extras.util.control;

/**
 * @author Bruno Salmon
 */
public enum HtmlInputAutocomplete {
    ON, // Default. Autocomplete is on (enabled)
    OFF, // Autocomplete is off (disabled)
    ADDRESS_LINE_1, // Expects the first line of the street address
    ADDRESS_LINE_2, // Expects the second line of the street address
    ADDRESS_LINE_3, // Expects the third line of the street address
    ADDRESS_LEVEL_1, // // Expects the first level of the address, e.g. the county
    ADDRESS_LEVEL_2, // // Expects the second level of the address, e.g. the city
    ADDRESS_LEVEL_3, // Expects the third level of the address
    ADDRESS_LEVEL_4, // Expects the fourth level of the address
    STREET_ADDRESS, // Expects the full street address
    COUNTRY, // Expects the country code
    COUNTRY_NAME, // // Expects the country name
    POSTAL_CODE, // Expects the post code
    NAME, // Expects the full name
    ADDITIONAL_NAME, // Expects the middle NAME
    FAMILY_NAME, // Expects the last NAME
    GIVEN_NAME, // Expects the first NAME
    HONORIC_PREFIX, // Expects the title, like "Mr", "Ms" etc.
    HONORIC_SUFFIX, // Expects the suffix, like "5", "Jr." etc.
    NICKNAME, // Expects the nickname
    ORGANIZATION_TITLE, // Expects the job title
    USERNAME, // Expects the username
    NEW_PASSWORD, // Expects a new password
    CURRENT_PASSWORD, // Expects the current password
    BDAY, // Expects the full birthday date
    BDAY_DAY, // Expects the day of the birthday date
    BDAY_MONTH, // Expects the month of the birthday date
    BDAY_YEAR, // Expects the year of the birthday date
    SEX, // Expects the gender
    ONE_TIME_CODE, // Expects a one time code for verification etc.
    ORGANIZATION, // Expects the company NAME
    CC_NAME, // Expects the credit card owner's full name
    CC_GIVEN_NAME, // Expects the credit card owner's first name
    CC_ADDITIONAL_NAME, // Expects the credit card owner's middle name
    CC_FAMILY_NAME, // Expects the credit card owner's full name
    CC_NUMBER, // Expects the credit card's number
    CC_EXP, // Expects the credit card's expiration date
    CC_EXP_MONTH, // Expects the credit card's expiration month
    CC_EXP_YEAR, // Expects the credit card's expiration year
    CC_CSC, // Expects the CVC code
    CC_TYPE, // Expects the credit card's type of payment
    TRANSACTION_CURRENCY, // Expects the currency
    TRANSACTION_AMOUNT, // Expects a number, the amount
    LANGUAGE, // Expects the preferred language
    URL, // Expects a web address
    EMAIL, // Expects the email address
    PHOTO, // Expects an image
    TEL, // Expects the full phone number
    TEL_COUNTRY_CODE, // Expects the country code of the phone number
    TEL_NATIONAL, // Expects the phone number with no country code
    TEL_AREA_CODE, // Expects the area code of the phone number
    TEL_LOCAL, // Expects the phone number with no country code and no area code
    TEL_LOCAL_PREFIX, // Expects the local prefix of the phone number
    TEL_LOCAL_SUFFIX, // Expects the local suffix of the phone number
    TEL_EXTENSION, // Expects the extension code of the phone number
    IMPP // Expects the url of an instant messaging protocol endpoint
}
