<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:svg="http://www.w3.org/2000/svg"
                exclude-result-prefixes="#all"
>
    <xsl:output
            method="xml"
            encoding="utf8"
            omit-xml-declaration="yes"
            indent="no" />

    <!-- a default catch is needed to suppress all unwanted nodes -->
    <xsl:template match="*">
        <xsl:apply-templates select="/xhtml:html/xhtml:body"/>
    </xsl:template>

    <xsl:template match="/xhtml:html/xhtml:body">
        <xsl:text  disable-output-escaping="yes">
********************
Supported SQL Syntax
********************

The EBNF and Railroad Diagrams for the supported SQL Syntax.
Kindly provided by Gunther Rademacher.

        </xsl:text>
        <xsl:apply-templates select="svg:svg"/>
    </xsl:template>

    <xsl:template match="svg:svg[preceding-sibling::*[1]/xhtml:a]">
<xsl:text  disable-output-escaping="yes">
=================================================
        </xsl:text>
        <xsl:value-of select="translate(preceding-sibling::*[1]/xhtml:a/text(),'\:','')"/>
        <xsl:text  disable-output-escaping="yes">
=================================================

        </xsl:text>

        <xsl:text  disable-output-escaping="yes">
.. raw:: html

        </xsl:text>

        <!-- SVG Diagram -->
        <xsl:copy-of select="."/>

        <!-- EBNF -->
        <xsl:copy-of select="following-sibling::*[1]"/>

        <!-- empty Line -->
        <xsl:text  disable-output-escaping="yes">

        </xsl:text>
    </xsl:template>
</xsl:stylesheet>