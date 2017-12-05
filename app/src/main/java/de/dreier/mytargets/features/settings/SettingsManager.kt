/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.Arrays;
import java.util.Map;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.scoreboard.EFileType;
import de.dreier.mytargets.features.scoreboard.ScoreboardConfiguration;
import de.dreier.mytargets.features.settings.backup.EBackupInterval;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.training.input.ETrainingScope;
import de.dreier.mytargets.features.training.input.SummaryConfiguration;
import de.dreier.mytargets.features.training.input.TargetView.EKeyboardType;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class SettingsManager {
    private static final SharedPreferences lastUsed = ApplicationInstance
            .getLastSharedPreferences();
    private static final SharedPreferences preferences = ApplicationInstance.Companion
            .getSharedPreferences();

    public static final String KEY_TIMER_WARN_TIME = "timer_warn_time";
    public static final String KEY_TIMER_WAIT_TIME = "timer_wait_time";
    public static final String KEY_TIMER_SHOOT_TIME = "timer_shoot_time";
    public static final String KEY_PROFILE_FIRST_NAME = "profile_first_name";
    public static final String KEY_PROFILE_LAST_NAME = "profile_last_name";
    public static final String KEY_PROFILE_BIRTHDAY = "profile_birthday";
    public static final String KEY_PROFILE_CLUB = "profile_club";
    public static final String KEY_PROFILE_LICENCE_NUMBER = "profile_licence_number";
    private static final String KEY_INPUT_KEEP_ABOVE_LOCKSCREEN = "input_keep_above_lockscreen";
    public static final String KEY_INPUT_SUMMARY_AVERAGE_OF = "input_summary_average_of";
    public static final String KEY_INPUT_ARROW_DIAMETER_SCALE = "input_arrow_diameter_scale";
    public static final String KEY_INPUT_TARGET_ZOOM = "input_target_zoom";
    public static final String KEY_INPUT_KEYBOARD_TYPE = "input_keyboard_type";
    private static final String KEY_INPUT_SUMMARY_SHOW_END = "input_summary_show_end";
    private static final String KEY_INPUT_SUMMARY_SHOW_ROUND = "input_summary_show_round";
    private static final String KEY_INPUT_SUMMARY_SHOW_TRAINING = "input_summary_show_training";
    private static final String KEY_INPUT_SUMMARY_SHOW_AVERAGE = "input_summary_show_average";
    public static final String KEY_SCOREBOARD_SHARE_FILE_TYPE = "scoreboard_share_file_type";
    private static final String KEY_BACKUP_INTERVAL = "backup_interval";
    private static final String KEY_DONATED = "donated";
    private static final String KEY_TIMER_KEEP_ABOVE_LOCKSCREEN = "timer_keep_above_lockscreen";
    private static final String KEY_TIMER_VIBRATE = "timer_vibrate";
    private static final String KEY_TIMER_SOUND = "timer_sound";
    private static final String KEY_STANDARD_ROUND = "standard_round";
    private static final String KEY_ARROW = "arrow";
    private static final String KEY_BOW = "bow";
    private static final String KEY_DISTANCE_VALUE = "distance";
    private static final String KEY_DISTANCE_UNIT = "unit";
    private static final String KEY_ARROWS_PER_END = "ppp";
    private static final String KEY_TARGET = "target";
    private static final String KEY_SCORING_STYLE = "scoring_style";
    private static final String KEY_TARGET_DIAMETER_VALUE = "size_target";
    private static final String KEY_TARGET_DIAMETER_UNIT = "unit_target";
    private static final String KEY_TIMER = "timer";
    private static final String KEY_NUMBERING_ENABLED = "numbering";
    private static final String KEY_INDOOR = "indoor";
    private static final String KEY_END_COUNT = "rounds";
    private static final String KEY_INPUT_MODE = "target_mode";
    public static final String KEY_SHOW_MODE = "show_mode";
    private static final String KEY_BACKUP_LOCATION = "backup_location";
    public static final String KEY_AGGREGATION_STRATEGY = "aggregation_strategy";
    private static final String KEY_STANDARD_ROUNDS_LAST_USED = "standard_round_last_used";
    private static final String KEY_INTRO_SHOWED = "intro_showed";
    private static final String KEY_OVERVIEW_SHOW_REACHED_SCORE = "overview_show_reached_score";
    private static final String KEY_OVERVIEW_SHOW_TOTAL_SCORE = "overview_show_total_score";
    private static final String KEY_OVERVIEW_SHOW_PERCENTAGE = "overview_show_percentage";
    private static final String KEY_OVERVIEW_SHOW_ARROW_AVERAGE = "overview_show_arrow_average";
    private static final String KEY_OVERVIEW_SHOT_SORTING = "overview_shot_sorting";
    private static final String KEY_OVERVIEW_SHOT_SORTING_SPOT = "overview_shot_sorting_spot";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE = "statistics_dispersion_pattern_file_type";
    public static final String KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY = "statistics_dispersion_pattern_aggregation_strategy";
    public static final String KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT = "statistics_dispersion_pattern_merge_spot";

    @NonNull
    public static Long getStandardRound() {
        return (long) lastUsed.getInt(KEY_STANDARD_ROUND, 32);
    }

    public static void setStandardRound(long id) {
        lastUsed.edit()
                .putInt(KEY_STANDARD_ROUND, (int) id)
                .apply();
    }

    @Nullable
    public static Long getArrow() {
        final int arrow = lastUsed.getInt(KEY_ARROW, -1);
        return arrow <= 0 ? null : (long) arrow;
    }

    public static void setArrow(@Nullable Long id) {
        lastUsed.edit()
                .putInt(KEY_ARROW, id == null ? -1 : (int) (long) id)
                .apply();
    }

    @Nullable
    public static Long getBow() {
        final int bow = lastUsed.getInt(KEY_BOW, -1);
        return bow <= 0 ? null : (long) bow;
    }

    public static void setBow(@Nullable Long id) {
        lastUsed.edit()
                .putInt(KEY_BOW, id == null ? -1 : (int) (long) id)
                .apply();
    }

    public static Dimension getDistance() {
        int distance = lastUsed.getInt(KEY_DISTANCE_VALUE, 10);
        String unit = lastUsed.getString(KEY_DISTANCE_UNIT, "m");
        return new Dimension(distance, unit);
    }

    public static void setDistance(@NonNull Dimension distance) {
        lastUsed.edit()
                .putInt(KEY_DISTANCE_VALUE, (int) distance.value)
                .putString(KEY_DISTANCE_UNIT, Dimension.Unit.toStringHandleNull(distance.unit))
                .apply();
    }

    public static int getShotsPerEnd() {
        return lastUsed.getInt(KEY_ARROWS_PER_END, 3);
    }

    public static void setShotsPerEnd(int shotsPerEnd) {
        lastUsed.edit()
                .putInt(KEY_ARROWS_PER_END, shotsPerEnd)
                .apply();
    }

    public static Target getTarget() {
        final int targetId = lastUsed.getInt(KEY_TARGET, 0);
        final int scoringStyle = lastUsed.getInt(KEY_SCORING_STYLE, 0);
        final int diameterValue = lastUsed.getInt(KEY_TARGET_DIAMETER_VALUE, 60);
        final String diameterUnit = lastUsed
                .getString(KEY_TARGET_DIAMETER_UNIT, CENTIMETER.toString());
        final Dimension diameter = new Dimension(diameterValue, diameterUnit);
        return new Target(targetId, scoringStyle, diameter);
    }

    public static void setTarget(@NonNull Target target) {
        lastUsed.edit()
                .putInt(KEY_TARGET, (int) (long) target.getId())
                .putInt(KEY_SCORING_STYLE, target.scoringStyle)
                .putInt(KEY_TARGET_DIAMETER_VALUE, (int) target.diameter.value)
                .putString(KEY_TARGET_DIAMETER_UNIT,
                        Dimension.Unit.toStringHandleNull(target.diameter.unit))
                .apply();
    }

    public static boolean getTimerEnabled() {
        return lastUsed.getBoolean(KEY_TIMER, false);
    }

    public static void setTimerEnabled(boolean enabled) {
        lastUsed.edit()
                .putBoolean(KEY_TIMER, enabled)
                .apply();
    }

    public static boolean getTimerKeepAboveLockscreen() {
        return preferences.getBoolean(KEY_TIMER_KEEP_ABOVE_LOCKSCREEN, true);
    }

    public static void setTimerKeepAboveLockscreen(boolean keepAboveLockscreen) {
        preferences
                .edit()
                .putBoolean(KEY_TIMER_KEEP_ABOVE_LOCKSCREEN, keepAboveLockscreen)
                .apply();
    }

    @NonNull
    public static TimerSettings getTimerSettings() {
        TimerSettings settings = new TimerSettings();
        settings.enabled = lastUsed.getBoolean(KEY_TIMER, false);
        settings.vibrate = preferences.getBoolean(KEY_TIMER_VIBRATE, false);
        settings.sound = preferences.getBoolean(KEY_TIMER_SOUND, true);
        settings.waitTime = getPrefTime(KEY_TIMER_WAIT_TIME, 10);
        settings.shootTime = getPrefTime(KEY_TIMER_SHOOT_TIME, 120);
        settings.warnTime = getPrefTime(KEY_TIMER_WARN_TIME, 30);
        return settings;
    }

    private static int getPrefTime(String key, int def) {
        try {
            return Integer.parseInt(preferences.getString(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static void setTimerSettings(@NonNull TimerSettings settings) {
        lastUsed.edit()
                .putBoolean(KEY_TIMER, settings.enabled)
                .apply();
        preferences
                .edit()
                .putBoolean(KEY_TIMER_VIBRATE, settings.vibrate)
                .putBoolean(KEY_TIMER_SOUND, settings.sound)
                .putString(KEY_TIMER_WAIT_TIME, String.valueOf(settings.waitTime))
                .putString(KEY_TIMER_SHOOT_TIME, String.valueOf(settings.shootTime))
                .putString(KEY_TIMER_WARN_TIME, String.valueOf(settings.warnTime))
                .apply();
    }

    public static boolean getArrowNumbersEnabled() {
        return lastUsed.getBoolean(KEY_NUMBERING_ENABLED, false);
    }

    public static void setArrowNumbersEnabled(boolean enabled) {
        lastUsed.edit()
                .putBoolean(KEY_NUMBERING_ENABLED, enabled)
                .apply();
    }

    public static boolean getIndoor() {
        return lastUsed.getBoolean(KEY_INDOOR, false);
    }

    public static void setIndoor(boolean indoor) {
        lastUsed.edit()
                .putBoolean(KEY_INDOOR, indoor)
                .apply();
    }

    public static int getEndCount() {
        return lastUsed.getInt(KEY_END_COUNT, 10);
    }

    public static void setEndCount(int endCount) {
        lastUsed.edit()
                .putInt(KEY_END_COUNT, endCount)
                .apply();
    }

    @NonNull
    public static TargetViewBase.EInputMethod getInputMethod() {
        return preferences
                .getBoolean(KEY_INPUT_MODE, false)
                ? TargetViewBase.EInputMethod.KEYBOARD
                : TargetViewBase.EInputMethod.PLOTTING;
    }

    public static void setInputMethod(TargetViewBase.EInputMethod inputMethod) {
        preferences
                .edit()
                .putBoolean(KEY_INPUT_MODE, inputMethod == TargetViewBase.EInputMethod.KEYBOARD)
                .apply();
    }

    public static boolean hasDonated() {
        return preferences
                .getBoolean(KEY_DONATED, false);
    }

    public static void setDonated(boolean donated) {
        preferences
                .edit()
                .putBoolean(KEY_DONATED, donated)
                .apply();
    }

    public static ETrainingScope getShowMode() {
        return ETrainingScope.valueOf(preferences
                .getString(KEY_SHOW_MODE, ETrainingScope.END.toString()));
    }

    public static void setShowMode(@NonNull ETrainingScope showMode) {
        preferences
                .edit()
                .putString(KEY_SHOW_MODE, showMode.toString())
                .apply();
    }

    public static EAggregationStrategy getAggregationStrategy() {
        return EAggregationStrategy.valueOf(preferences
                .getString(KEY_AGGREGATION_STRATEGY, EAggregationStrategy.AVERAGE.toString()));
    }

    public static void setAggregationStrategy(@NonNull EAggregationStrategy aggregationStrategy) {
        preferences
                .edit()
                .putString(KEY_AGGREGATION_STRATEGY, aggregationStrategy.toString())
                .apply();
    }

    @NonNull
    public static String getProfileFirstName() {
        return preferences
                .getString(KEY_PROFILE_FIRST_NAME, "");
    }

    public static void setProfileFirstName(String firstName) {
        preferences
                .edit()
                .putString(KEY_PROFILE_FIRST_NAME, firstName)
                .apply();
    }

    @NonNull
    public static String getProfileLastName() {
        return preferences
                .getString(KEY_PROFILE_LAST_NAME, "");
    }

    public static void setProfileLastName(String lastName) {
        preferences
                .edit()
                .putString(KEY_PROFILE_LAST_NAME, lastName)
                .apply();
    }

    public static String getProfileFullName() {
        return String.format("%s %s", getProfileFirstName(), getProfileLastName()).trim();
    }

    @NonNull
    public static String getProfileClub() {
        return preferences
                .getString(KEY_PROFILE_CLUB, "");
    }

    public static void setProfileClub(String club) {
        preferences
                .edit()
                .putString(KEY_PROFILE_CLUB, club)
                .apply();
    }

    @NonNull
    public static String getProfileLicenceNumber() {
        return preferences
                .getString(KEY_PROFILE_LICENCE_NUMBER, "");
    }

    public static void setProfileLicenceNumber(String licenceNumber) {
        preferences
                .edit()
                .putString(KEY_PROFILE_LICENCE_NUMBER, licenceNumber)
                .apply();
    }

    public static LocalDate getProfileBirthDay() {
        String date = preferences
                .getString(KEY_PROFILE_BIRTHDAY, "");
        if (date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date);
    }

    public static void setProfileBirthDay(@NonNull LocalDate birthDay) {
        preferences.edit()
                .putString(KEY_PROFILE_BIRTHDAY, birthDay.toString())
                .apply();
    }

    public static String getProfileBirthDayFormatted() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null) {
            return null;
        }
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(birthDay);
    }

    public static int getProfileAge() {
        final LocalDate birthDay = getProfileBirthDay();
        if (birthDay == null) {
            return -1;
        }
        return Period.between(birthDay, LocalDate.now()).getYears();
    }

    public static boolean getInputKeepAboveLockscreen() {
        return preferences.getBoolean(KEY_INPUT_KEEP_ABOVE_LOCKSCREEN, true);
    }

    public static void setInputKeepAboveLockscreen(boolean keepAboveLockscreen) {
        preferences
                .edit()
                .putBoolean(KEY_INPUT_KEEP_ABOVE_LOCKSCREEN, keepAboveLockscreen)
                .apply();
    }

    public static float getInputArrowDiameterScale() {
        return Float.parseFloat(preferences
                .getString(KEY_INPUT_ARROW_DIAMETER_SCALE, "1.0"));
    }

    public static void setInputArrowDiameterScale(float diameterScale) {
        preferences.edit()
                .putString(KEY_INPUT_ARROW_DIAMETER_SCALE, String.valueOf(diameterScale))
                .apply();
    }

    public static float getInputTargetZoom() {
        return Float.parseFloat(preferences
                .getString(KEY_INPUT_TARGET_ZOOM, "3.0"));
    }

    public static void setInputTargetZoom(float targetZoom) {
        preferences.edit()
                .putString(KEY_INPUT_TARGET_ZOOM, String.valueOf(targetZoom))
                .apply();
    }

    public static EKeyboardType getInputKeyboardType() {
        return EKeyboardType.valueOf(preferences
                .getString(KEY_INPUT_KEYBOARD_TYPE, EKeyboardType.RIGHT.name()));
    }

    public static void setInputKeyboardType(@NonNull EKeyboardType type) {
        preferences
                .edit()
                .putString(KEY_INPUT_KEYBOARD_TYPE, type.name())
                .apply();
    }

    public static EFileType getScoreboardShareFileType() {
        return EFileType.valueOf(preferences
                .getString(KEY_SCOREBOARD_SHARE_FILE_TYPE, EFileType.PDF.name()));
    }

    public static void setScoreboardShareFileType(@NonNull EFileType fileType) {
        preferences
                .edit()
                .putString(KEY_SCOREBOARD_SHARE_FILE_TYPE, fileType.name())
                .apply();
    }

    public static EFileType getStatisticsDispersionPatternFileType() {
        return EFileType.valueOf(preferences
                .getString(KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE, EFileType.JPG.name()));
    }

    public static void setStatisticsDispersionPatternFileType(@NonNull EFileType fileType) {
        preferences
                .edit()
                .putString(KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE, fileType.name())
                .apply();
    }

    public static EAggregationStrategy getStatisticsDispersionPatternAggregationStrategy() {
        return EAggregationStrategy.valueOf(preferences
                .getString(KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY, EAggregationStrategy.AVERAGE.toString()));
    }

    public static void setStatisticsDispersionPatternAggregationStrategy(@NonNull EAggregationStrategy aggregationStrategy) {
        preferences
                .edit()
                .putString(KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY, aggregationStrategy.toString())
                .apply();
    }

    public static boolean getStatisticsDispersionPatternMergeSpot() {
        return preferences
                .getBoolean(KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT, false);
    }

    public static void setStatisticsDispersionPatternMergeSpot(boolean mergeSpot) {
        preferences
                .edit()
                .putBoolean(KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT, mergeSpot)
                .apply();
    }

    public static EBackupLocation getBackupLocation() {
        final String defaultLocation = EBackupLocation.INTERNAL_STORAGE.name();
        String location = preferences.getString(KEY_BACKUP_LOCATION, defaultLocation);
        return EBackupLocation.valueOf(location);
    }

    public static void setBackupLocation(@NonNull EBackupLocation location) {
        preferences
                .edit()
                .putString(KEY_BACKUP_LOCATION, location.name())
                .apply();
    }

    public static EBackupInterval getBackupInterval() {
        return EBackupInterval.valueOf(preferences.getString(KEY_BACKUP_INTERVAL,
                EBackupInterval.WEEKLY.name()));
    }

    public static void setBackupInterval(@NonNull EBackupInterval interval) {
        preferences.edit()
                .putString(KEY_BACKUP_INTERVAL, interval.name())
                .apply();
    }

    public static void setLanguage(String language) {
        preferences
                .edit()
                .putString(KEY_LANGUAGE, language)
                .apply();
    }

    public static Map<Long, Integer> getStandardRoundsLastUsed() {
        String[] split = lastUsed.getString(KEY_STANDARD_ROUNDS_LAST_USED, "").split(",");
        return Stream.of(Arrays.asList(split))
                .filterNot(String::isEmpty)
                .map(entry -> entry.split(":"))
                .toMap(a -> Long.valueOf(a[0]), a -> Integer.valueOf(a[1]));
    }

    public static void setStandardRoundsLastUsed(@NonNull Map<Long, Integer> ids) {
        lastUsed.edit()
                .putString(KEY_STANDARD_ROUNDS_LAST_USED, Stream.of(ids)
                        .map(id -> id.getKey() + ":" + id.getValue())
                        .joining(","))
                .apply();
    }

    public static boolean shouldShowIntroActivity() {
        return preferences
                .getBoolean(KEY_INTRO_SHOWED, true);
    }

    public static void setShouldShowIntroActivity(boolean shouldShow) {
        preferences.edit()
                .putBoolean(KEY_INTRO_SHOWED, shouldShow)
                .apply();
    }

    @NonNull
    public static SummaryConfiguration getInputSummaryConfiguration() {
        SummaryConfiguration config = new SummaryConfiguration();
        config.showEnd = preferences.getBoolean(KEY_INPUT_SUMMARY_SHOW_END, true);
        config.showRound = preferences.getBoolean(KEY_INPUT_SUMMARY_SHOW_ROUND, true);
        config.showTraining = preferences.getBoolean(KEY_INPUT_SUMMARY_SHOW_TRAINING, false);
        config.showAverage = preferences.getBoolean(KEY_INPUT_SUMMARY_SHOW_AVERAGE, true);
        config.averageScope = ETrainingScope
                .valueOf(preferences.getString(KEY_INPUT_SUMMARY_AVERAGE_OF, "ROUND"));
        return config;
    }

    public static void setInputSummaryConfiguration(@NonNull SummaryConfiguration configuration) {
        preferences.edit()
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_END, configuration.showEnd)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_ROUND, configuration.showRound)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_TRAINING, configuration.showTraining)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_AVERAGE, configuration.showAverage)
                .putString(KEY_INPUT_SUMMARY_AVERAGE_OF, configuration.averageScope.name())
                .apply();
    }

    @NonNull
    public static Score.Configuration getScoreConfiguration() {
        Score.Configuration config = new Score.Configuration();
        config.showReachedScore = preferences.getBoolean(KEY_OVERVIEW_SHOW_REACHED_SCORE, true);
        config.showTotalScore = preferences.getBoolean(KEY_OVERVIEW_SHOW_TOTAL_SCORE, true);
        config.showPercentage = preferences.getBoolean(KEY_OVERVIEW_SHOW_PERCENTAGE, false);
        config.showAverage = preferences.getBoolean(KEY_OVERVIEW_SHOW_ARROW_AVERAGE, false);
        return config;
    }

    public static void setScoreConfiguration(@NonNull Score.Configuration configuration) {
        preferences.edit()
                .putBoolean(KEY_OVERVIEW_SHOW_REACHED_SCORE, configuration.showReachedScore)
                .putBoolean(KEY_OVERVIEW_SHOW_TOTAL_SCORE, configuration.showTotalScore)
                .putBoolean(KEY_OVERVIEW_SHOW_PERCENTAGE, configuration.showPercentage)
                .putBoolean(KEY_OVERVIEW_SHOW_ARROW_AVERAGE, configuration.showAverage)
                .apply();
    }

    public static boolean shouldSortTarget(@NonNull Target target) {
        return preferences.getBoolean(KEY_OVERVIEW_SHOT_SORTING, true) &&
                (target.getModel().getFaceCount() == 1 ||
                        preferences.getBoolean(KEY_OVERVIEW_SHOT_SORTING_SPOT, false));
    }

    @NonNull
    public static ScoreboardConfiguration getScoreboardConfiguration() {
        ScoreboardConfiguration config = new ScoreboardConfiguration();
        config.showTitle = preferences.getBoolean("scoreboard_title", true);
        config.showProperties = preferences.getBoolean("scoreboard_properties", true);
        config.showTable = preferences.getBoolean("scoreboard_table", true);
        config.showStatistics = preferences.getBoolean("scoreboard_statistics", true);
        config.showComments = preferences.getBoolean("scoreboard_comments", true);
        config.showPointsColored = preferences.getBoolean("scoreboard_points_colored", true);
        config.showSignature = preferences.getBoolean("scoreboard_signature", true);
        return config;
    }
}