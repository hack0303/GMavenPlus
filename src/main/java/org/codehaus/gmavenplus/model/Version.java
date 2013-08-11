/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmavenplus.model;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;


/**
 * Container for Version information in the form of
 * <tt>major.minor.revision-tag</tt>.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author Keegan Witt
 */
public class Version implements Comparable<Version> {
    /**
     * The version major number.
     */
    private int major;
    /**
     * The version minor number.
     */
    private int minor;
    /**
     * The version revision.
     */
    private int revision;
    /**
     * The version tag.
     */
    private String tag;

    /**
     * Constructs a new version object with the specified parameters.
     *
     * @param newMajor The version major number
     * @param newMinor The version minor number
     * @param newRevision The version revision number
     * @param newTag The version tag string
     */
    public Version(final int newMajor, final int newMinor, final int newRevision, final String newTag) {
        if (newMajor < 0 || newMinor < 0 || newRevision < 0) {
            // note we don't check the tag since it can be null
            throw new IllegalArgumentException("Major must be >= 0 and minor >= 0 and revision >= 0.");
        }

        this.major = newMajor;
        this.minor = newMinor;
        this.revision = newRevision;
        if (newTag == null || !newTag.isEmpty()) {
            this.tag = newTag;
        } else if (newTag.isEmpty()) {
            this.tag = null;
        }
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param newMajor The version major number
     * @param newMinor The version minor number
     * @param newRevision The version revision number
     */
    public Version(final int newMajor, final int newMinor, final int newRevision) {
        this(newMajor, newMinor, newRevision, null);
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param newMajor The version major number
     * @param newMinor The version minor number
     */
    public Version(final int newMajor, final int newMinor) {
        this(newMajor, newMinor, 0);
    }

    /**
     * Constructs a new Version object with the specified parameters.
     *
     * @param newMajor The version major number
     */
    public Version(final int newMajor) {
        this(newMajor, 0);
    }

    /**
     * Parses a new Version object from a string.
     *
     * @param version The version string to parse
     * @return The version parsed from the string
     */
    public static Version parseFromString(final String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version must not be null or empty.");
        }
        String[] split = version.split("[.-]", 4);
        try {
            int tagIdx = 3;
            int major = Integer.parseInt(split[0]);
            int minor = 0;
            int revision = 0;
            StringBuilder tag = new StringBuilder();
            if (split.length >= 2) {
                try {
                    minor = Integer.parseInt(split[1]);
                } catch (NumberFormatException nfe) {
                    // version string must not have specified a minor version, leave minor as 0 and append to tag instead
                    tag.append(split[1]);
                    tagIdx = 1;
                    if (tagIdx != 3) {
                        tag.append("-");
                    }
                }
            }
            if (split.length >= 3) {
                try {
                    revision = Integer.parseInt(split[2]);
                } catch (NumberFormatException nfe) {
                    // version string must not have specified a revision version, leave revision as 0 and append to tag instead
                    tag.append(split[2]);
                    tagIdx = 2;
                    if (tagIdx != 3) {
                        tag.append("-");
                    }
                }
            }
            if (split.length >= 4) {
                for (int i = tagIdx; i < split.length; i++) {
                    if (i > tagIdx) {
                        tag.append("-");
                    }
                    tag.append(split[i]);
                }
            }
            return new Version(major, minor, revision, tag.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Major, minor, and revision must be integers.", e);
        }
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code for this object
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return Objects.hashCode(major, minor, revision, tag);
    }

    /**
     * Determines whether the specified object is equal to this object.
     *
     * @param obj The object to compare to this object
     * @return <code>true</code> if the specified object is equal to this object, <code>false</code> otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object obj) {
        if (obj instanceof Version) {
            final Version other = (Version) obj;
            return major == other.major
                    && minor == other.minor
                    && revision == other.revision
                    && Objects.equal(tag, other.tag);
        } else {
            return false;
        }
    }

    /**
     * Returns a String representation of this object.
     *
     * @return The String representation of this object
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuilder buff = new StringBuilder();

        buff.append(major)
                .append(".").append(minor)
                .append(".").append(revision);
        if (tag != null) {
            buff.append("-").append(tag);
        }

        return buff.toString();
    }

    /**
     * Compares two versions objects.  Note that if the major, minor, and revision are all
     * the same, tags are compared with {@link java.lang.String#compareTo(String) String.compareTo()}.
     *
     * @param version The version to compare this version to
     * @return <code>0</code> if the version is equal to this version, <code>1</code> if the version is greater than
     *         this version, or <code>-1</code> if the version is lower than this version.
     */
    public final int compareTo(final Version version) {
        return ComparisonChain.start()
                .compare(major, version.major)
                .compare(minor, version.minor)
                .compare(revision, version.revision)
                .compare(tag, version.tag, Ordering.natural().nullsLast())
                .result();
    }

    /**
     * Gets the version major number.
     *
     * @return The major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * Sets the version major number.
     *
     * @param newMajor The major version number to set
     * @return This object (for fluent invocation)
     */
    public Version setMajor(final int newMajor) {
        this.major = newMajor;
        return this;
    }

    /**
     * Gets the version minor number.
     *
     * @return The version minor number
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Sets the version minor number.
     *
     * @param newMinor The version minor number to set
     * @return This object (for fluent invocation)
     */
    public Version setMinor(final int newMinor) {
        this.minor = newMinor;
        return this;
    }

    /**
     * Gets the version revision number.
     *
     * @return The version revision number
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Sets the version revision number.
     *
     * @param newRevision The revision number to set
     * @return This object (for fluent invocation)
     */
    public Version setRevision(final int newRevision) {
        this.revision = newRevision;
        return this;
    }

    /**
     * Gets the version tag string.
     *
     * @return The version tag string
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the version tag string.
     *
     * @param newTag The version tag string to set
     * @return This object (for fluent invocation)
     */
    public Version setTag(final String newTag) {
        this.tag = newTag;
        return this;
    }

}