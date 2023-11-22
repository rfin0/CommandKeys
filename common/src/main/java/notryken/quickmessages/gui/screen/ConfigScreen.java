package notryken.quickmessages.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import notryken.quickmessages.QuickMessages;
import notryken.quickmessages.gui.component.listwidget.ConfigListWidget;
import notryken.quickmessages.gui.component.listwidget.ConfigListWidgetDual;
import notryken.quickmessages.gui.component.listwidget.ConfigListWidgetMono;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends OptionsSubScreen {
    private ConfigListWidget listWidget;
    private int flag;

    public ConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.literal("Quick Messages"));
        flag = 0;
    }

    /**
     * Utilises a control flag to determine what type of ConfigListWidget to
     * initialize with. Obviously not ideal, used as a workaround since
     * ListWidget creation requires specifying dimensions, which I haven't yet
     * figured out how to obtain when Minecraft.getInstance.screen == null.
     */
    public ConfigScreen(Screen parent, Options options, Component title, int flag) {
        super(parent, options, title);
        this.flag = flag;
    }

    public ConfigScreen(Screen parent, Options options, Component title, ConfigListWidget listWidget) {
        super(parent, options, title);
        this.listWidget = listWidget;
    }

    @Override
    protected void init() {
        if (listWidget == null) {
            Minecraft client = Minecraft.getInstance();
            if (flag == 0) {
                listWidget = new ConfigListWidgetMono(client, width, height,
                        32, height - 32, 25, lastScreen, title);
            }
            else if (flag == 1) {
                listWidget = new ConfigListWidgetDual(client, width, height,
                        32, height - 32, 25, lastScreen, title);
            }
            else {
                throw new IllegalArgumentException("Flag must be 0 or 1, received " + flag);
            }
        }
        addWidget(listWidget);
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                .size(240, 20)
                .pos(this.width / 2 - 120, this.height - 27)
                .build());
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Using keyReleased to avoid key press overlap with next screen.
        listWidget.onKey(keyCode, scanCode);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.listWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xffffff);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(context);
    }

    @Override
    public void onClose() {
        if (!(lastScreen instanceof ConfigScreen)) {
            QuickMessages.config().purge();
            QuickMessages.config().syncMonoMap();
            QuickMessages.config().writeChanges();
        }
        super.onClose();
    }
}
