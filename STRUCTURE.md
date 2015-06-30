Explanation of the structure of the GFX Backend
----

All renderable things have are a  **renderer** method which returns a *Renderer*. All rendering is done in a *Renderer* which has a **render** method. The world has a **getRenderers** method which returns an array of all the renderers
of the world (ie the tanks and the bullets). This is called by the *GFXManager*.

The *UIManager* controls all the input as well as the GUI. It has a **getRenderers** method which which returns all of the GUI renderers. This is called in **GFXManager.renderAll**
after the world has been rendered and then all the renderers are rendered. The *KeyBoardManager* responds to events and those events change flags which can be found in a *HashMap* called flags
in the *UIManager* companion object. The UI Renderers can then act on these flags allowing the user control of the UI.