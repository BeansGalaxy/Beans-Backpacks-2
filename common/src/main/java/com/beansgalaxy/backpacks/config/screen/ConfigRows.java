package com.beansgalaxy.backpacks.config.screen;

import com.beansgalaxy.backpacks.Constants;
import com.beansgalaxy.backpacks.config.ConfigScreen;
import com.beansgalaxy.backpacks.config.IConfig;
import com.beansgalaxy.backpacks.config.types.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ConfigRows extends ContainerObjectSelectionList<ConfigRows.ConfigLabel> {
      public final IConfig config;
      protected ConfigScreen screen;

      public ConfigRows(ConfigScreen screen, Minecraft minecraft, IConfig config) {
            super(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight() * 2, 35, screen.height - 32, 25);
            //    minecraft, width,        height,             y0, y1,               spacing);

            this.screen = screen;
            this.config = config;

            for (ConfigLabel row : getRows()) {
                  addEntry(row);
            }
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int i) {
            ConfigLabel focused = getFocused();
            if (focused != null) focused.hungryClick(mouseX, mouseY, i);

            return super.mouseClicked(mouseX, mouseY, i);
      }

      public abstract List<ConfigLabel> getRows();

      public class ConfigLabel extends ContainerObjectSelectionList.Entry<ConfigLabel> {
            public final Component name;

            public ConfigLabel(Component name) {
                  this.name = name;
            }

            public void resetToDefault() {
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawCenteredString(minecraft.font, name, x + rowWidth / 2, y + 8, 0xFFFFFFFF);
                  guiGraphics.hLine(x + 30, x + rowWidth - 30, y + 18, 0xFFFFFFFF);
            }

            @Override // BUTTONS GO HERE
            public List<? extends GuiEventListener> children() {
                  return List.of();
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of();
            }

            public void hungryClick(double mouseX, double mouseY, int i) {

            }

            public void onSave() {

            }
      }

      public class BoolConfigRow extends ConfigLabel {
            private final BoolConfigVariant bool;
            private final Button button;

            public BoolConfigRow(BoolConfigVariant bool) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + bool.name(), bool.name()));
                  this.bool = bool;
                  this.button = Button.builder(getTranslatedValue(bool.get()), in -> {
                        bool.set(!bool.get());
                  }).bounds(0, 0, 70, 20).build();
            }

            @Override
            public void resetToDefault() {
                  bool.set(bool.getDefau());
            }

            static Component getTranslatedValue(boolean value) {
                  return value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawString(minecraft.font, bool.name(), x, y + 6, 0xFFFFFFFF);
                  button.setMessage(getTranslatedValue(bool.get()));
                  button.setX(x + rowWidth - button.getWidth());
                  button.setY(y);
                  button.render(guiGraphics, mouseX, mouseY, delta);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(button);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(button);
            }
      }

      public class EnumConfigRow<T extends Enum<T>> extends ConfigLabel {
            private final EnumConfigVariant<T> value;
            private final T[] values;
            private final Button button;

            public EnumConfigRow(EnumConfigVariant<T> value, T[] values) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  this.value = value;
                  this.values = values;
                  this.button = Button.builder(getTranslatedValue(value.get()), in -> {
                        value.set(getNextValue());
                  }).bounds(0, 0, 70, 20).build();
            }

            private T getNextValue() {
                  Iterator<T> iterator = Arrays.stream(values).iterator();
                  T t = value.get();
                  while (iterator.hasNext()) {
                        if (t.equals(iterator.next())) {
                              if (iterator.hasNext())
                                    return iterator.next();
                              else
                                    return values[0];
                        }
                  }
                  return t;
            }

            @Override
            public void resetToDefault() {
                  value.set(value.getDefau());
            }

            Component getTranslatedValue(T value) {
                  return Component.translatableWithFallback("screen.beansbackpacks.config.enum." + value.name(), value.name());
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawString(minecraft.font, value.name(), x, y + 6, 0xFFFFFFFF);
                  button.setMessage(getTranslatedValue(value.get()));
                  button.setX(x + rowWidth - button.getWidth());
                  button.setY(y);
                  button.render(guiGraphics, mouseX, mouseY, delta);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(button);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(button);
            }
      }

      public class IntConfigRow extends ConfigLabel {
            private final IntConfigVariant value;
            private final IntButton button;

            public IntConfigRow(IntConfigVariant value) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  this.value = value;
                  this.button = IntButton.builder(in -> {}, value::get)
                              .onEnter(in -> value.set(Mth.clamp(in, value.min, value.max)))
                              .onClose(in -> close())
                              .bounds(0, 0, 70, 20).build();
            }

            @Override
            public void resetToDefault() {
                  value.set(value.getDefau());
            }

            private void close() {
                  ConfigRows.this.setFocused(null);
                  button.setFocused(false);
                  setFocused(false);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawString(minecraft.font, value.name(), x, y + 6, 0xFFFFFFFF);
                  button.setX(x + rowWidth - button.getWidth());
                  button.setY(y);
                  button.render(guiGraphics, mouseX, mouseY, delta);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(button);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(button);
            }
      }

      public class ItemListConfigRow extends ConfigLabel {
            private final HSetConfigVariant<Item> value;
            private final EditBox searchBox;
            private SearchTree<Item> searchTree = new SearchTree<>() {
                  @Override
                  public @NotNull List<Item> search(@NotNull String s) {
                        Set<ResourceLocation> itemIDs = BuiltInRegistries.ITEM.keySet();
                        List<ResourceLocation> list = new ArrayList<>(itemIDs.stream().filter(in -> in.toString().contains(s)).toList());
                        if (s.contains(":")) {
                              list.sort(Comparator.comparingInt(x -> {
                                    String key = x.toString();
                                    return key.compareTo(s);
                              }));
                        } else {
                              list.sort(Comparator.comparingInt(x -> {
                                    String key = x.getPath();
                                    int i = Math.abs(key.compareTo(s));
                                    return i;
                              }));
                        }

                        List<Item> itemList = new ArrayList<>(list.stream().map(BuiltInRegistries.ITEM::get).toList());
                        itemList.removeAll(value.get());
                        return itemList;
                  }
            };

            public ItemListConfigRow(HSetConfigVariant<Item> value) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  this.value = value;
                  this.searchBox = new EditBox(minecraft.font, 0, 0, 120, 20, Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  searchBox.setHint(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()).withStyle(ChatFormatting.GRAY));
            }

            @Override
            public void resetToDefault() {
                  value.set(value.getDefau());
            }

            private List<Item> searchResults = List.of();
            @Override
            public boolean keyPressed(int i, int $$1, int $$2) {
                  boolean b = super.keyPressed(i, $$1, $$2);
                  searchResults = getSearchResults();
                  return b;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int i) {
                  if (i == 0) {
                        if (hoveredStoredItem != null)
                              value.get().remove(hoveredStoredItem.item);
                  }
                  searchResults = getSearchResults();
                  return super.mouseClicked(mouseX, mouseY, i);
            }

            @Override
            public void hungryClick(double mouseX, double mouseY, int i) {
                  if (i == 0 && hoveredSearchItem != null) {
                        value.get().add(hoveredSearchItem.item);
                        searchResults = getSearchResults();
                  }
            }

            @Override
            public void render(GuiGraphics gui, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  searchBox.setX(x);
                  searchBox.setY(y);
                  searchBox.render(gui, mouseX, mouseY, delta);
                  if (!searchBox.isFocused())
                        searchBox.setValue("");
                  else if (!searchResults.isEmpty()) {
                        double slotX = (x - mouseX);
                        double slotY = (mouseY - y + 2);
                        boolean checkHovering = slotX > 0 && slotY > 0;

                        gui.pose().pushPose();
                        gui.pose().translate(x - 10, y - 2, 0);
                        gui.pose().scale(0.5f, 0.5f, 0.5f);

                        hoveredSearchItem = null;
                        for (int i = 0; i < searchResults.size() && i < 36; i++) {
                              Item next = searchResults.get(i);
                              int entriesX = (i / 3);
                              int entriesY = (i % 3);
                              gui.renderItem(next.getDefaultInstance(), -entriesX * 16, entriesY * 16);
                              if (checkHovering && Mth.floor(slotX) / 8 == entriesX && Mth.floor(slotY) / 8 == entriesY)
                                    hoveredSearchItem = new HoveredItem(next, entriesX, entriesY);
                        } gui.pose().popPose();

                        if (hoveredSearchItem != null) {
                              gui.pose().pushPose();
                              gui.pose().translate(0, 0, 300);
                              gui.drawString(minecraft.font, Component.literal("✔"), x - hoveredSearchItem.x * 8 - 9, y - 2 + hoveredSearchItem.y * 8, 0xFF77EE77);
                              gui.pose().popPose();
                        }
                  }

                  renderStoredItems(gui, y, x, mouseX, mouseY);
            }

            private void renderStoredItems(GuiGraphics gui, int y, int x, int mouseX, int mouseY) {
                  int x1 = x + searchBox.getWidth() + 2;
                  double slotX = (mouseX - x1);
                  double slotY = (mouseY - y + 2);
                  boolean checkHovering = slotX > 0 && slotY > 0;

                  Iterator<Item> iterator = value.get().iterator();
                  gui.pose().pushPose();
                  gui.pose().translate(x1, y - 2, 0);
                  gui.pose().scale(0.5f, 0.5f, 0.5f);

                  int i = 0;
                  hoveredStoredItem = null;
                  while (iterator.hasNext() && i < 38) {
                        Item next = iterator.next();
                        int entriesX = (i / 3);
                        int entriesY = (i % 3);
                        gui.renderItem(next.getDefaultInstance(), entriesX * 16, entriesY * 16);
                        if (checkHovering && Mth.floor(slotX) / 8 == entriesX && Mth.floor(slotY) / 8 == entriesY) {
                              hoveredStoredItem = new HoveredItem(next, entriesX, entriesY);
                        }
                        i++;
                  }
                  gui.pose().popPose();

                  int size = value.get().size();
                  if (size > 38) {
                        gui.drawString(minecraft.font, Component.literal("+" + (size - 38)), x1 + (12) * 8, y - 2 + (2) * 8, 0xFFFFFFFF);
                  }

                  if (hoveredStoredItem != null) {
                        gui.pose().pushPose();
                        gui.pose().translate(0, 0, 300);
                        gui.drawString(minecraft.font, Component.literal("✘"), x1 + 1 + hoveredStoredItem.x * 8, y - 2 + hoveredStoredItem.y * 8, 0xFFFFAAAA);
                        gui.pose().popPose();
                  }
            }

            private HoveredItem hoveredStoredItem = null;
            private HoveredItem hoveredSearchItem = null;
            private record HoveredItem(Item item, int x, int y) {

            }

            private List<Item> getSearchResults() {
                  String search = searchBox.getValue();
                  if (search.isEmpty()) return List.of();

                  return searchTree.search(search.toLowerCase(Locale.ROOT));
            }

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(searchBox);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(searchBox);
            }
      }

      public class MoveBackSlotConfigRow extends ConfigLabel {
            private final ListConfigVariant<Integer> value;
            private final Button button;

            public MoveBackSlotConfigRow(ListConfigVariant<Integer> value) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  this.value = value;
                  this.button = Button.builder(Component.translatable("screen.beansbackpacks.config.back_slot_pos.edit"), in -> {
                        MoveElementConfigScreen screen = MoveElementConfigScreen.Builder.create()
                                    .background(InventoryScreen.INVENTORY_LOCATION)
                                    .backgroundSize(176, 166)
                                    .elementSize(16, 16)
                                    .elementPos(value.get(0), value.get(1))
                                    .onSave((x, y) -> {
                                          value.get().set(0, x);
                                          value.get().set(1, y);
                                    }).build(ConfigRows.this.screen);
                        minecraft.setScreen(screen);
                  }).bounds(0, 0, 70, 20).build();
            }

            @Override
            public void resetToDefault() {
                  value.set(new ArrayList<>(value.getDefau()));
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  guiGraphics.drawString(minecraft.font, value.name(), x, y + 6, 0xFFFFFFFF);
                  button.setX(x + rowWidth - button.getWidth());
                  button.setY(y);
                  button.render(guiGraphics, mouseX, mouseY, delta);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(button);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(button);
            }
      }

      public class IntMapConfigRow<K> extends ConfigLabel {
            private final MapConfigVariant<K, Integer> value;
            private final EditBox keyBox;
            private final IntButton intButton;
            private final Button add;
            private final Button expand;
            private boolean expanded = false;

            public IntMapConfigRow(MapConfigVariant<K, Integer> value) {
                  super(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  this.value = value;
                  int x = getRowLeft();

                  this.expand = Button.builder(Component.literal(">"), this::toggleExpanded)
                              .bounds(x - 1, 0, 20, 20)
                              .build();

                  this.keyBox = new EditBox(minecraft.font, x + 43, 0, (getRowWidth() - 40) - (47), 20, Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()));
                  keyBox.setHint(Component.translatableWithFallback("screen.beansbackpacks.config." + value.name(), value.name()).withStyle(ChatFormatting.GRAY));

                  this.intButton = IntButton.builder(in -> {}, null)
                              .bounds(x + getRowWidth() - 40, 0, 40, 20)
                              .build();

                  this.add = Button.builder(Component.literal("+"), in -> {
                        String key = keyBox.getValue();
                        int entry = intButton.storedValue;
                        if (!Constants.isEmpty(key) && value.validate.test(key, entry)) {
                              value.put(key, value.clamp.apply(entry));
                        }
                  }).bounds(x + 20, 0, 20, 20)
                              .build();
            }

            @Override
            public void onSave() {
            }

            private void toggleExpanded(Button in) {
                  expanded = !expanded;
                  if (expanded) {
                        in.setMessage(Component.literal("<"));
                  } else {
                        in.setMessage(Component.literal(">"));
                  }
            }

            @Override
            public void hungryClick(double mouseX, double mouseY, int i) {
                  if (i == 0 && hoveredEntry != null && mouseX < getRowLeft() && hoveredEntry.y - 2 < mouseY && hoveredEntry.y + 8 > mouseY)
                        value.get().remove(hoveredEntry.key);

                  super.hungryClick(mouseX, mouseY, i);
            }

            @Override
            public void resetToDefault() {
                  value.set(new HashMap<>(value.getDefau()));
            }

            private float lastDelta = 0;
            private int scrollText = -80;
            @Override
            public void render(GuiGraphics gui, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
                  keyBox.setY(y);
                  keyBox.render(gui, mouseX, mouseY, delta);
                  intButton.setY(y);
                  intButton.render(gui, mouseX, mouseY, delta);
                  add.setY(y);
                  add.render(gui, mouseX, mouseY, delta);
                  expand.setY(y);
                  expand.render(gui, mouseX, mouseY, delta);
                  if (!isFocused()) keyBox.setValue("");
                  if (expanded) {
                        if (!isFocused())
                              toggleExpanded(expand);
                        else
                              renderSideMenu(gui, x, mouseX, mouseY, delta);
                  }
            }

            private void renderSideMenu(GuiGraphics gui, int x, int mouseX, int mouseY, float delta) {
                  int i = 0;
                  hoveredEntry = null;
                  gui.enableScissor(10, 0, x - 1, screen.height);
                  for (String key : value.get().keySet())
                  {
                        Integer entry = value.get().get(key);
                        MutableComponent line = Component.literal(key + ": " + entry);
                        int lineX = 10;
                        int lineY = 60 + 10 * i;
                        if (mouseX < x - 10 && lineY - 2 < mouseY && lineY + 8 > mouseY)
                        {
                              line.withStyle(ChatFormatting.RED);
                              hoveredEntry = new HoveredEntry(key, lineY);
                              int width = minecraft.font.width(line);
                              if (width > x) {
                                    if (lastDelta > delta)
                                          scrollText++;
                                    lineX = 10 - Mth.clamp(scrollText, 0, width - x + 14);
                                    if (scrollText > width)
                                          scrollText = -80;
                              } else scrollText = -20;
                        }
                        gui.drawString(minecraft.font, line, lineX, lineY, 0xFFFFFFFF);
                        i++;
                  }
                  gui.disableScissor();

                  if (hoveredEntry != null)
                        gui.drawString(minecraft.font, Component.literal("✘").withStyle(ChatFormatting.RED), 3, hoveredEntry.y + 1, 0xFFFFFF);

                  lastDelta = delta;
            }

            private HoveredEntry hoveredEntry = null;
            public record HoveredEntry(String key, int y) {}

            @Override
            public List<? extends GuiEventListener> children() {
                  return List.of(keyBox, intButton, add, expand);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                  return List.of(keyBox, intButton, add, expand);
            }
      }



}
