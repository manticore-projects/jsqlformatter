# -*- coding: utf-8 -*-
import sys, os

sys.path.append('/home/are/.local/lib/python3.9/site-packages')
import sphinx_redactor_theme, rtcat_sphinx_theme

#
# parsing maven pom.xml
from xml.etree import ElementTree as et

namespaces = {'xmlns' : 'http://maven.apache.org/POM/4.0.0'}

tree = et.parse('../../../pom.xml')
root = tree.getroot()

artifactId = root.find("xmlns:artifactId", namespaces=namespaces).text
projectName = root.find("xmlns:name", namespaces=namespaces).text
versionName = root.find("xmlns:version", namespaces=namespaces).text
developerName = root.find("xmlns:developers/xmlns:developer[1]/xmlns:name", namespaces=namespaces).text

#deps = root.findall("xmlns:dependency[xmlns:artifactId='jsqlparser']", namespaces=namespaces)
#print(deps)

#for d in deps:
#   artifactId = d.find("xmlns:artifactId", namespaces=namespaces)
#   version1   = d.find("xmlns:version", namespaces=namespaces)
#   print(artifactId.text + '\t' + version1.text)

####################################

email_support = "andreas@manticore-projects.com"

rst_epilog = """
.. |EMAIL| replace:: {0}
.. |VERSION| replace:: {1}
""".format(email_support, versionName)

rst_prolog = """
.. |EMAIL| replace:: {0}
.. |VERSION| replace:: {1}
""".format(email_support, versionName)

extlinks = {'downloads': ('https://github.com/manticore-projects/jsqlformatter/releases/download/%s', '%s')}

#rst_epilog = """
#.. |email_support| replace:: {0}
#""".format(email_support)

project = projectName
copyright = u'2020, ' + developerName
release = versionName

# General options
needs_sphinx = '1.0'
master_doc = 'index'
pygments_style = 'colorful'
add_function_parentheses = True

extensions = ['recommonmark', 'sphinx.ext.autodoc', 'sphinxcontrib.plantuml', 'sphinx_rtd_theme', 'sphinx_git', 'sphinx.ext.githubpages', 'sphinx_tabs.tabs', 'sphinx.ext.extlinks', 'sphinx-prompt', 'sphinx_substitution_extensions']

templates_path = ['templates']
exclude_trees = ['.build']
source_encoding = 'utf-8-sig'

# HTML options

#html_theme = 'sphinx_redactor_theme'
#html_theme_path = [sphinx_redactor_theme.get_html_theme_path()]

html_theme = "rtcat_sphinx_theme"
html_theme_path = [rtcat_sphinx_theme.get_html_theme_path()]

#html_theme = "sphinxdoc"
#html_theme_path = [rtcat_sphinx_theme.get_html_theme_path()]

#html_theme = "sphinx_rtd_theme"
html_theme_options = {
    'analytics_id': 'UA-XXXXXXX-1',  #  Provided by Google in your dashboard
    'analytics_anonymize_ip': False,
    'logo_only': False,
    'display_version': True,
    'prev_next_buttons_location': 'bottom',
    'style_external_links': False,
    'vcs_pageview_mode': '',
    'style_nav_header_background': '#2980B9',

    # Toc options
    'collapse_navigation': False,
    'sticky_navigation': False,
    'navigation_depth': -1,
    'includehidden': True,
    'titles_only': False
}


html_short_title = artifactId
htmlhelp_basename = artifactId + '-doc'
html_use_index = True
html_show_sourcelink = False
html_static_path = ['_static']
html_logo = '_static/manticore_logo.png'

# PlantUML options
plantuml = os.getenv('plantuml')

