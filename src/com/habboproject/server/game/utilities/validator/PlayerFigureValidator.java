package com.habboproject.server.game.utilities.validator;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Steve Winfield (IDK Project)
 */
public class PlayerFigureValidator {

    private static Map<Integer, Map<Integer, PlayerFigureColor>> palettes;
    private static Map<String, PlayerFigureSetType> setTypes;
    private static Map<String, Map<Integer, List<String>>> mandatorySetTypes;

    public static void loadFigureData() {
        try {
            final File figureDataFile = new File("config/figuredata.xml");
            final Document furnidataDocument;

            if (!figureDataFile.exists()) {
                furnidataDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Comet.class.getResourceAsStream("/config/figuredata.xml"));
                FileUtils.copyURLToFile(Comet.class.getResource("/config/figuredata.xml"), figureDataFile);
            } else {
                furnidataDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(figureDataFile);
            }

            final Element furnidata = (Element) furnidataDocument.getElementsByTagName("figuredata").item(0);

            PlayerFigureValidator.palettes = new ConcurrentHashMap<>();
            PlayerFigureValidator.setTypes = new ConcurrentHashMap<>();

            /**
             * Gender and club mandatories
             */
            PlayerFigureValidator.mandatorySetTypes = new ConcurrentHashMap<>();
            PlayerFigureValidator.mandatorySetTypes.put("m", new ConcurrentHashMap<>());
            PlayerFigureValidator.mandatorySetTypes.get("m").put(0, new ArrayList<>());
            PlayerFigureValidator.mandatorySetTypes.get("m").put(1, new ArrayList<>());
            PlayerFigureValidator.mandatorySetTypes.get("m").put(2, new ArrayList<>());
            PlayerFigureValidator.mandatorySetTypes.put("f", new ConcurrentHashMap<>());
            PlayerFigureValidator.mandatorySetTypes.get("f").put(0, new ArrayList<>());
            PlayerFigureValidator.mandatorySetTypes.get("f").put(1, new ArrayList<>());
            PlayerFigureValidator.mandatorySetTypes.get("f").put(2, new ArrayList<>());

            final NodeList palettes = furnidata.getElementsByTagName("palette");

            for (int paletteIndex = 0; paletteIndex < palettes.getLength(); ++paletteIndex) {
                final Node paletteNode = palettes.item(paletteIndex);

                if (paletteNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element paletteElement = (Element) paletteNode;

                    final int paletteId = Integer.valueOf(paletteElement.getAttribute("id"));
                    PlayerFigureValidator.palettes.put(paletteId, new ConcurrentHashMap<>());

                    final NodeList colors = paletteElement.getElementsByTagName("color");

                    for (int colorIndex = 0; colorIndex < colors.getLength(); ++colorIndex) {
                        final Node colorNode = colors.item(colorIndex);

                        if (colorNode.getNodeType() == Node.ELEMENT_NODE) {
                            final Element colorElement = (Element) colorNode;
                            final int colorId = Integer.valueOf(colorElement.getAttribute("id"));

                            PlayerFigureValidator.palettes.get(paletteId).put(colorId, new PlayerFigureColor(Integer.valueOf(colorElement.getAttribute("club")), Integer.valueOf(colorElement.getAttribute("selectable")) == 1));
                        }
                    }
                }
            }

            final NodeList setTypes = furnidata.getElementsByTagName("settype");

            for (int setTypeIndex = 0; setTypeIndex < setTypes.getLength(); ++setTypeIndex) {
                final Node setTypeNode = setTypes.item(setTypeIndex);

                if (setTypeNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element setTypeElement = (Element) setTypeNode;
                    final String typeName = setTypeElement.getAttribute("type").toLowerCase();

                    if (Integer.valueOf(setTypeElement.getAttribute("mand_m_0")) > 0) {
                        PlayerFigureValidator.mandatorySetTypes.get("m").get(0).add(typeName);
                    }

                    if (Integer.valueOf(setTypeElement.getAttribute("mand_f_0")) > 0) {
                        PlayerFigureValidator.mandatorySetTypes.get("f").get(0).add(typeName);
                    }

                    if (Integer.valueOf(setTypeElement.getAttribute("mand_m_1")) > 0) {
                        PlayerFigureValidator.mandatorySetTypes.get("m").get(1).add(typeName);
                        PlayerFigureValidator.mandatorySetTypes.get("m").get(2).add(typeName);
                    }

                    if (Integer.valueOf(setTypeElement.getAttribute("mand_f_1")) > 0) {
                        PlayerFigureValidator.mandatorySetTypes.get("f").get(1).add(typeName);
                        PlayerFigureValidator.mandatorySetTypes.get("f").get(2).add(typeName);
                    }

                    final Map<Integer, PlayerFigureSet> setMap = new ConcurrentHashMap<>();
                    final NodeList sets = setTypeElement.getElementsByTagName("set");

                    for (int setIndex = 0; setIndex < sets.getLength(); ++setIndex) {
                        final Node setNode = sets.item(setIndex);

                        if (setNode.getNodeType() == Node.ELEMENT_NODE) {
                            final Element setElement = (Element) setNode;
                            final int setId = Integer.valueOf(setElement.getAttribute("id"));

                            int colorCount = 0;
                            final NodeList parts = setElement.getElementsByTagName("part");

                            for (int partIndex = 0; partIndex < parts.getLength(); ++partIndex) {
                                final Node partNode = parts.item(partIndex);

                                if (partNode.getNodeType() == Node.ELEMENT_NODE) {
                                    final Element partElement = (Element) partNode;
                                    final int colorIndex = Integer.valueOf(partElement.getAttribute("colorindex"));

                                    if (Integer.valueOf(partElement.getAttribute("colorable")) > 0 && colorIndex > colorCount) {
                                        colorCount = colorIndex;
                                    }
                                }
                            }

                            setMap.put(setId, new PlayerFigureSet(setElement.getAttribute("gender").toLowerCase(), Integer.valueOf(setElement.getAttribute("club")), Integer.valueOf(setElement.getAttribute("colorable")) > 0, Integer.valueOf(setElement.getAttribute("selectable")) > 0, colorCount));
                        }
                    }

                    PlayerFigureValidator.setTypes.put(typeName, new PlayerFigureSetType(typeName, Integer.valueOf(setTypeElement.getAttribute("paletteid")), setMap));
                }
            }
        } catch (Exception e) {
            Comet.getServer().getLogger().warn("Error while initializing the PlayerFigureValidator", e);
        }
    }

    public static boolean isValidFigureCode(final String figureCode, final String gender) {
        if (!CometSettings.playerFigureValidation) {
            return true;
        }

        if (figureCode == null) {
            return false;
        }

        try {
            if (!gender.equals("m") && !gender.equals("f")) {
                return false;
            }

            final String[] sets = figureCode.split("\\.");
            final List<String> mandatorySets = PlayerFigureValidator.mandatorySetTypes.get(gender).get(2);

            if (sets.length < mandatorySets.size()) {
                return false;
            }

            final List<String> containedSets = new ArrayList<>();

            for (final String set : sets) {
                final String[] setData = set.split("-");

                if (setData.length < 3) {
                    return false;
                }

                final String setType = setData[0].toLowerCase();

                if (!PlayerFigureValidator.setTypes.containsKey(setType)) {
                    return false;
                }

                final PlayerFigureSetType setTypeInstance = PlayerFigureValidator.setTypes.get(setType);
                final Map<Integer, PlayerFigureSet> setMap = setTypeInstance.getSets();
                final int setId = Integer.valueOf(setData[1]);

                if (!setMap.containsKey(setId)) {
                    return false;
                }

                final PlayerFigureSet setInstance = setMap.get(setId);

                if (!setInstance.isSelectable() || (setData.length - 2) < setInstance.getColorCount()) {
                    return false;
                }

                for (int i = 0; i < setInstance.getColorCount(); ++i) {
                    final int colorId = Integer.valueOf(setData[i + 2]);

                    if (!PlayerFigureValidator.palettes.get(setTypeInstance.getPaletteId()).containsKey(colorId)) {
                        return false;
                    }

                    final PlayerFigureColor colorInstance = PlayerFigureValidator.palettes.get(setTypeInstance.getPaletteId()).get(colorId);

                    if (!colorInstance.isSelectable()) {
                        return false;
                    }
                }

                containedSets.add(setType);
            }

            for (final String mandatorySet : mandatorySets) {
                if (!containedSets.contains(mandatorySet)) {
                    return false;
                }
            }

            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}