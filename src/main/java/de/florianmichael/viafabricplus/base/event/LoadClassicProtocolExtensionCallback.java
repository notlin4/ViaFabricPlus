/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.base.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;

/**
 * This event is fired when a classic protocol extension is loaded.
 */
public interface LoadClassicProtocolExtensionCallback {

    Event<LoadClassicProtocolExtensionCallback> EVENT = EventFactory.createArrayBacked(LoadClassicProtocolExtensionCallback.class, listeners -> classicProtocolExtension -> {
        for (LoadClassicProtocolExtensionCallback listener : listeners) {
            listener.onLoadClassicProtocolExtension(classicProtocolExtension);
        }
    });

    void onLoadClassicProtocolExtension(final ClassicProtocolExtension classicProtocolExtension);
}
