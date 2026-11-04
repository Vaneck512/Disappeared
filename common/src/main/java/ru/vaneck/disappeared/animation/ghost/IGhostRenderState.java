package ru.vaneck.disappeared.animation.ghost;

public interface IGhostRenderState {
    float disappeared$getGhostAlpha();
    boolean disappeared$isGhost();
    void disappeared$setGhost(Ghost<?,?> ghost);
    Ghost<?,?> disappeared$getGhost();
}
