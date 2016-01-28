#! /usr/bin/perl

use strict;
use IO::File;

if($#ARGV + 1 != 2) {
	print "usage: perl FindUniqueEpsts.pl <inputFile> <outputFile>\n";
	exit(0);
}

my $inputFile = new IO::File($ARGV[0], "r") or die $!;
my $outputFile = new IO::File($ARGV[1], "w") or die $!;

# line format: 
#Peptide ID index 0
#Peptide Sequence index 1
#Genome ID index 2
#Start index 3
#End index 4
#Strand index 5
#Reading Frame index 6
#RT Peptide Sequence index 7
#ePST Start index 8
#ePST End index 9
#ePST index 10
#ePSTLength index 11
#translated ePST index 12
#start codon index 13
#peptide probability index 14
#peptide count index 15

# output <inputLine>\t<ePST Probability>\t<ePST Count>

#ePST Probability = Prod_i(peptideProbability_i)
#ePST Count = Sum_i(peptideCount_i)

# take the highest probability peptide as the ePST Probability

my $ePSTProbability = 0;
my $ePSTCount = 0;
my $ePSTStart = 0;
my $ePSTEnd = 0;

# each of the peptides that make up the current ePST
my @peptides;

# skip the header line
my $headerLine = $inputFile->getline;
chomp($headerLine);
print $outputFile "$headerLine\tePST Probability\tePST Count\n";

while(my $line = $inputFile->getline) {
	chomp($line);
	my @split = split("\t", $line);

	# same ePST as previous?
	if($split[8] == $ePSTStart && $split[9] == $ePSTEnd) {
		$ePSTProbability += (1 - $split[14]) * $split[15];
		$ePSTCount += $split[15];
		push(@peptides, $line);
	} else {
		# print the previous ePST
		if($ePSTCount > 0) {
			$ePSTProbability /= $ePSTCount;
		}
		$ePSTProbability = 1 - $ePSTProbability;
		for my $peptide (@peptides) {
			print $outputFile "$peptide\t$ePSTProbability\t$ePSTCount\n";
		}

		@peptides = ();
		push(@peptides, $line);
		$ePSTStart = $split[8];
		$ePSTEnd = $split[9];
		$ePSTProbability = (1 - $split[14]) * $split[15];
		$ePSTCount = $split[15];
	}
}

$inputFile->close;
$outputFile->close;
