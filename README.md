# CiteHelper
Git-compatible literature management.

## Quickstart
*CiteHelper uses separate files for each literature entry and supports optional same-name PDF-files aside.*

Start by creating a project in CiteHelper and import BibTeX-files using `Data -> Import BibTeX-file` menu option. CiteHelper will automatically create files in current project's working directory for each literature entry.
You can associate PDF files to an entry by selecting it and choose `Assign PDF`.

While open, CiteHelper scans for content changes in the working directory, so manual changes are immediately seen in CiteHelper.

Export the collection using `Data -> Export library`.

## Manual setup
CiteHelper does not require anything but a simple directory setup.
For each library, setup a folder like this:

```
 root
  |- .citeproject
  |- barabasi1999emergence.bib
  |- barabasi1999emergence.pdf
  |- werbach2012.bib
  |- werbach2012.pdf
  \- ...
```
CiteHelper will aggregate all files to a library-file on export.

## Project metafile
The `.citeproject` metafile in a project working directory specifies name and a relative export target for that project's library:

```
projectname=Some project name
exporttarget=library.bib
```