# -*- coding: utf-8 -*-
#
# Sphinx configuration for the JSQLFormatter documentation site.
#
# Version substitutions (|JSQLFORMATTER_VERSION| and friends) are injected by
# the Gradle `sphinx` task -- they are deliberately NOT defined here.

# ---------------------------------------------------------------------------
# General
# ---------------------------------------------------------------------------

needs_sphinx = '5.0'
add_function_parentheses = True

extensions = [
    'myst_parser',
    'sphinx.ext.autodoc',
    'sphinx.ext.autosectionlabel',
    'sphinx.ext.extlinks',
    'sphinx-prompt',
    'sphinx_substitution_extensions',
    'sphinx_issues',
    'sphinx_tabs.tabs',
    'pygments.sphinxext',

    # Adds a copy button to every code block. Worth the dependency on a site
    # that is mostly code blocks:  pip install sphinx-copybutton
    # 'sphinx_copybutton',
]

# Was pointing at the JSqlParser tracker, so every :issue:`n` role on this site
# linked to the wrong repository.
issues_github_path = "manticore-projects/jsqlformatter"

extlinks = {
    'jsqlparser': ('https://github.com/JSQLParser/JSqlParser/%s', 'JSqlParser %s'),
}

source_encoding = 'utf-8-sig'
pygments_style = 'friendly'
show_sphinx = False
master_doc = 'index'

# autosectionlabel across a doc set this size collides on common headings
# ("Maven", "Gradle", "Java"). Prefixing with the document name disambiguates.
autosectionlabel_prefix_document = True

exclude_patterns = [
    '_themes',
    '_static/css',

    # Railroad diagrams retired after 5.4 -- ~33k lines of generated inline SVG
    # for two pages nobody linked to. Delete the files once you are happy.
    'syntax.rst',
    'syntax_snapshot.rst',
]

# ---------------------------------------------------------------------------
# HTML
# ---------------------------------------------------------------------------

html_theme = "manticore_sphinx_theme"
html_theme_path = ["_themes"]
html_title = "JSQLFormatter"
html_short_title = "JSQLFormatter"
htmlhelp_basename = "JSQLFormatter" + '-doc'
html_use_index = True
html_show_sourcelink = False
html_static_path = ['_static']
html_logo = '_static/manticore_logo.png'
html_favicon = '_static/favicon.ico'
html_css_files = ["css/theme.css"]
html_copy_source = False
html_last_updated_fmt = '%Y-%m-%d'

html_theme_options = {
    'canonical_url': 'https://manticore-projects.com/JSQLFormatter/',
    'style_external_links': True,
    'collapse_navigation': True,
    'sticky_navigation': True,
    'navigation_depth': 4,
    'includehidden': True,
    'titles_only': False,
}

# Landing-page cards rendered by the Manticore theme.
html_context = {
    'landing_page': {
        'menu': [
            {'title': 'Install',
             'url': 'install.html'},
            {'title': 'How to use it',
             'url': 'usage.html'},
            {'title': 'Samples',
             'url': 'samples.html'},
            {'title': 'Online Demo',
             'url': 'http://jsqlformatter.manticore-projects.com'},
            {'title': 'GitHub',
             'url': 'https://github.com/manticore-projects/jsqlformatter'},
            {'title': 'Issue Tracker',
             'url': 'https://github.com/manticore-projects/jsqlformatter/issues'},
        ]
    }
}