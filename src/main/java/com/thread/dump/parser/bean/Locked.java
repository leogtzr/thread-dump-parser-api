package com.thread.dump.parser.bean;

import java.util.Objects;

public class Locked {

    private String lockID;
    private String lockedObjectName;

    public Locked(final String lockID, final String lockedObjectName) {
        this.lockID = lockID;
        this.lockedObjectName = lockedObjectName;
    }

    public Locked() {}

    public String getLockID() {
        return lockID;
    }

    public void setLockID(final String lockID) {
        this.lockID = lockID;
    }

    public String getLockedObjectName() {
        return lockedObjectName;
    }

    public void setLockedObjectName(final String lockedObjectName) {
        this.lockedObjectName = lockedObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Locked locked = (Locked) o;
        return Objects.equals(lockID, locked.lockID) && Objects.equals(lockedObjectName, locked.lockedObjectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockID, lockedObjectName);
    }

    @Override
    public String toString() {
        return "Locked{" +
                "lockID='" + lockID + '\'' +
                ", lockedObjectName='" + lockedObjectName + '\'' +
                '}';
    }
}
