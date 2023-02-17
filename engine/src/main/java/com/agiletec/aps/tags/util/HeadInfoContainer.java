/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.aps.tags.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contenitore di informazioni da inserire nella testata della pagina html.
 *
 * @author W.Ambu
 */
public class HeadInfoContainer implements Serializable {

    private final Map<String, List<String>> container = new HashMap<>();

    /**
     * Inserisce nel contenitore un'informazione di un dato tipo. Nel caso dei fogli di stile, il tipo è "StyleSheet" e
     * l'informazione è una stringa contenente il nome del foglio di stile.
     *
     * @param type Il tipo di informazione da aggiungere.
     * @param info L'informazione da aggiungere.
     */
    public void addInfo(String type, String info) {
        List<String> infos = this.container.computeIfAbsent(type, t -> new ArrayList<>());
        if (!infos.contains(info)) {
            infos.add(info);
        }
    }

    /**
     * Restituisce una collezione di informazioni in base al tipo.
     *
     * @param type Il tipo delle informazioni richieste.
     * @return Una collezione di informazioni.
     */
    public List<String> getInfos(String type) {
        return this.container.get(type);
    }

}