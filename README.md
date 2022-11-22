# CraftFX

Minecraft's UI system is very specialized. In the very limited use-cases for which Mojang designed it initially (server browsing, world creation, etc.), it performs quite well.

However, as the scope of the game and its tools increase through modding, Minecraft's UI can begin to severely underperform. This is where CraftFX comes in.

## What is CraftFX?

CraftFX is an attempt to integrate [JavaFX](https://openjfx.io/), a popular Java UI platform, with Minecraft. Using modern JavaFX features such as Pixel Buffers, and a few technologies developed for [Scaffold Editor](https://github.com/Sam54123/Scaffold), CraftFX allows modders to seamlessly integrate JavaFX applications with Minecraft.

**CraftFX is *not* a replacement for Minecraft's UI.** 

Because JavaFX is not compatible with GLFW and LWJGL, each JavaFX app opens in its own window. Due to this, CraftFX is inadequate for gameplay-oriented tasks such as crafting or inventory management. Rather, CraftFX is designed for UI-oriented applications such as level editors. One could think of it as a bridge between standalone programs and Minecraft's game engine.


