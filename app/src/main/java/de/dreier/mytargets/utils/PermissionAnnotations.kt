package de.dreier.mytargets.utils

/**
 * Simple annotation stubs to replace PermissionsDispatcher annotations
 * These are just markers - the actual permission logic is handled manually
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RuntimePermissions

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NeedsPermission(vararg val value: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnShowRationale(vararg val value: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnPermissionDenied(vararg val value: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnNeverAskAgain(vararg val value: String)

