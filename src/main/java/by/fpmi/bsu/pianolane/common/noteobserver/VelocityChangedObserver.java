package by.fpmi.bsu.pianolane.common.noteobserver;

public interface VelocityChangedObserver {

    void onVelocityChanged(Integer noteId, int newVelocity);
}
