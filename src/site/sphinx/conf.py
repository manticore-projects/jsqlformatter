# -*- coding: utf-8 -*-
import sys, os

sys.path.append('/home/are/.local/lib/python3.9/site-packages')
import sphinx_redactor_theme, rtcat_sphinx_theme

#rst_epilog = """
#.. |EMAIL| replace:: {0}
#.. |VERSION| replace:: {1}
#""".format(email_support, versionName)

artifactId = "com.manticore-projects.jsqlformatter"
projectName = "JSQLFormatter"
versionName = "0.1.12"
developerName = "Andreas Reichel"
email_support = "andreas@manticore-projects.com"

rst_prolog = """
.. |EMAIL| replace:: {0}
.. |VERSION| replace:: {1}
""".format(email_support, versionName)

extlinks = {'downloads': ('https://github.com/manticore-projects/jsqlformatter/releases/download/%s', '%s')}

#rst_epilog = """
#.. |email_support| replace:: {0}
#""".format(email_support)

project = projectName
copyright = u'2021, ' + developerName
release = versionName

# General options
needs_sphinx = '1.0'
master_doc = 'index'
pygments_style = 'stata'
add_function_parentheses = True

extensions = ['sphinx.ext.autodoc', 'sphinx_rtd_theme', 'sphinx_git', 'sphinx.ext.githubpages', 'sphinx_tabs.tabs', 'sphinx.ext.extlinks', 'sphinx-prompt', 'sphinx_substitution_extensions']

issues_github_path = "manticore-projects/jsqlformatter"

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
    #'analytics_anonymize_ip': False,
    'logo_only': False,
    'display_version': True,
    'style_nav_header_background': '#39698a',
    #'prev_next_buttons_location': 'bottom',
    #'style_external_links': False,
    #'vcs_pageview_mode': '',
    

    # Toc options
    'collapse_navigation': False,
    'sticky_navigation': False,
    'navigation_depth': -1,
    #'includehidden': True,
    #'titles_only': False
}


html_short_title = artifactId
htmlhelp_basename = artifactId + '-doc'
html_use_index = True
html_show_sourcelink = False
html_static_path = ['_static']
html_logo = '_static/manticore_logo.png'

# PlantUML options
plantuml = os.getenv('plantuml')

