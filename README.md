## What is this ?
Japscan-downloader is a simple tool to extract and download mangas from japscan website.

## Prerequisites

JRE 6 at least

## Basic Usage

There is 3 modes available.

#### The 'full' mode
Download all chapters of the given manga

Example: `java -Dmode=full -Dmanga=nanatsu-no-taizai -jar japscan-downloader.jar`

#### The 'chapter' mode
Download only one chapter

Example: `java -Dmode=chapter -Dchapter=12 -Dmanga=nanatsu-no-taizai -jar japscan-downloader.jar`

#### The 'range' mode
Download a range of chapters

Example: `java -Dmode=range -Drange=12-15 -Dmanga=nanatsu-no-taizai -jar japscan-downloader.jar`

## Where to find manga name ?
You need to know the manga name used by japscan to download it.
- Go on japscan website and search your manga
- Open for example the first chapter of the manga
- Watch the name of the manga i the URL, for example: https://www.japscan.cc/lecture-en-ligne/**nanatsu-no-taizai**/274/2.html

## Advanced Usage
Sometimes, the japscan URL have prefix in chapter name. In this case, you have to use japscan-downloader with an other one parameter that provide this prefix

For Example if the URL is like that: https://www.japscan.cc/lecture-en-ligne/dragon-ball/**volume-1**/1.html

You have to add -Dprefix=volume-

To download this chapter it give: `java -Dmode=chapter -Dchapter=1 -Dmanga=dragon-ball -Dprefix=volume- -jar japscan-downloader.jar`
